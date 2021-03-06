package com.mlpj.ontology.jenawork;

import java.io.FileReader;

import com.mlpj.ontology.data.Person;
import com.mlpj.ontology.util.Constant;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class InitJena {

    private static QueryExecution qe;
    private static String ontoFile = Constant.FILE;
    //private static String ontoFile = Constant.FILE2;

    public static ResultSet execQuery(String queryString) {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, "");
                Query query = QueryFactory.create(queryString);
                //Execute the query and obtain results
                qe = QueryExecutionFactory.create(query, ontoModel);
                ResultSet results = qe.execSelect();
                return results;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
        return null;
    }


    public static String getItems2(String query) {
        ResultSet resultSet = execQuery(query);
        String result = "";
        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            String u = pretty(solution.get("U").toString());//class
            String y = pretty(solution.get("Y").toString());//label predicate
            String u1 = pretty(solution.get("U1").toString());//label class
            String z = pretty(solution.get("Z").toString());//object
            if (Constant.STRING_LIST.contains(u)) continue;
            if (u1.contains("@en") || y.contains("@en") || y.contains("@es") || z.contains("@en")) continue;
            result += u1 + "~" + pretty(solution.get("X").toString()) + "~" + y + "~" + z + "\n";

        }
        qe.close();
        return result;


    }


    public static String getItems3(String query) {
        ResultSet resultSet = execQuery(query);
        String result = "";
        List<String> listClass = new ArrayList<>();
        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            String u = pretty(solution.get("U").toString());//class
            String y = pretty(solution.get("Y").toString());//label predicate
            String u1 = pretty(solution.get("U1").toString());//label class
            String z = pretty(solution.get("Z").toString());//object
            if (Constant.STRING_LIST.contains(u)) continue;
            if (u1.contains("@en") || y.contains("@en") || y.contains("@es") || z.contains("@en")) continue;
            if (listClass.contains(u1)) continue;
            result += u1 + "~" + pretty(solution.get("X").toString()) + "~" + y + "~" + z + "~" + u + "\n";
            listClass.add(u1);
        }
        qe.close();
        return result;


    }

    public static String getItems4(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String result = "";

        while (resultSet.hasNext()) {

            QuerySolution solution = resultSet.nextSolution();
            result += solution.get("X").toString().split("#")[1] + ", ";
        }
        qe.close();
        return result.replaceAll("_", " ");
    }


    public static String getItems5(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String result = "";
        int x = 0;
        while (resultSet.hasNext()) {
            x++;
            QuerySolution solution = resultSet.nextSolution();
            result += solution.get("X") + " t??? ch???c t???i " + solution.get("Z").toString() + "\n";

        }
        qe.close();
        return result;
    }

    public static String getItems6(String queryString) {
        ResultSet resultSet = execQuery(queryString);
        String result = "";

        while (resultSet.hasNext()) {

            QuerySolution solution = resultSet.nextSolution();
            String[] s = solution.get("X").toString().split("#");
            result += s.length > 1 ? s[1] : s[0] + ", ";
        }
        qe.close();
        return result.replaceAll("_", " ");
    }


    public static void insert(String filename) {
        try {
            OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            InputStream in = FileManager.get().open(Constant.FILE);
            m.read(in, null);
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(System.getProperty("user.dir") + "/" + filename);
            JSONObject object = (JSONObject) jsonParser.parse(reader);
            Set<String> set = object.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject jsonObject = (JSONObject) object.get(key);
                key = key.replaceAll(" ", "_");
                insert(m, key, Constant.PREFIX + "Person");
                JSONObject sinh = (JSONObject) jsonObject.get("Sinh");
                Set<String> set1 = sinh.keySet();
                Iterator<String> iterator1 = set1.iterator();
                while(iterator1.hasNext()){
                    String key1 = iterator1.next();
                    insert(m, key, "hasBorn", sinh.get(key1)+" (theo "+key1+ ")");
                }

                JSONObject mat = (JSONObject) jsonObject.get("M???t");
                Set<String> set2 = mat.keySet();
                Iterator<String> iterator2 = set2.iterator();
                while(iterator2.hasNext()){
                    String key2 = iterator2.next();
                    insert(m, key, "hasDied", mat.get(key2)+" (theo "+key2+ ")");
                }



            }
            OutputStream output = new FileOutputStream(Constant.FILE);
            m.writeAll(output, "RDF/XML", null);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean insert(Person person) {
        try {

            insert(person.getName(), Constant.PREFIX + "Person");
            //insert(person.getName(), Constant.PREFIX_OWL + "NamedIndividual");

            insert(person.getName(), "hasBorn", person.getHasBorn());
            insert(person.getName(), "hasBornAt", person.getHasBornAt());
            insert(person.getName(), "hasDied", person.getHasDied());
            insert(person.getName(), "hasJob", person.getHasJob());
            insert(person.getName(), "hasPeriod", person.getHasPeriod());
            insert(person.getName(), "hasSuccessor", person.getHasSuccessor());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //khai bao type cho instant
    public static void insert(OntModel m, String name, String object) throws Exception {
        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        Resource a0 = m.createResource(Constant.PREFIX + name);
        Resource a1 = m.createResource(object);
        m.add(a1, RDF.type, OWL.Class);
        m.add(a0, RDF.type, a1);
    }


    //khai bao type cho instant
    public static void insert(String name, String object) throws Exception {

        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.createResource(Constant.PREFIX + name);
        Resource a1 = m.createResource(object);

        m.add(a1, RDF.type, OWL.Class);
        m.add(a0, RDF.type, a1);

        OutputStream output = new FileOutputStream(Constant.FILE);
        m.writeAll(output, "RDF/XML", null);
        output.close();


    }

    //insert label
    public static void insert2(String name, String object) throws Exception {

        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.createResource(Constant.PREFIX + name);


        m.add(a0, RDFS.label, object);

        OutputStream output = new FileOutputStream(Constant.FILE);
        m.writeAll(output, "RDF/XML", null);
        output.close();


    }
    //Tao canh noi object voi thuoc tinh cua no

    public static void insert(OntModel m, String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }


        Resource a0 = m.createResource(Constant.PREFIX + name);

        Property property = m.createProperty(Constant.PREFIX + predicate);


        m.add(a0, property, object);


    }


    //Tao canh noi object voi thuoc tinh cua no
    public static void insert(String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.createResource(Constant.PREFIX + name);

        Property property = m.createProperty(Constant.PREFIX + predicate);


        m.add(a0, property, object);
        OutputStream output = new FileOutputStream(Constant.FILE);
        m.writeAll(output, "RDF/XML", null);
        output.close();


    }


    //Tao canh noi 2 object
    public static void insert2(String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.createResource(Constant.PREFIX + name);

        Property property = m.createProperty(Constant.PREFIX + predicate);
        Resource node = m.createResource(Constant.PREFIX + object);

        m.add(a0, property, node);
        OutputStream output = new FileOutputStream(Constant.FILE);
        m.writeAll(output, "RDF/XML", null);
        output.close();


    }

    //predicate t??y ch???nh, kh??ng m???c ?????nh prefix l?? foaf
    public static void insert3(String name, String predicate, String object) throws Exception {

        if (name == null || object == null || predicate == null || name == "" || object == "" || predicate == "") {
            return;
        }

        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.createResource(Constant.PREFIX + name);

        Property property = m.createProperty(predicate);


        m.add(a0, property, object);
        OutputStream output = new FileOutputStream(Constant.FILE);
        m.writeAll(output, "RDF/XML", null);
        output.close();


    }

    //insert label cho predicate t??y ch???nh, kh??ng m???c ?????nh prefix l?? foaf
    public static void insert4(String name, String object) throws Exception {

        if (name == null || object == null || name == "" || object == "") {
            return;
        }
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        InputStream in = FileManager.get().open(Constant.FILE);
        m.read(in, null);
        Resource a0 = m.createResource(name);


        m.add(a0, RDFS.label, object);

        OutputStream output = new FileOutputStream(Constant.FILE);
        m.writeAll(output, "RDF/XML", null);
        output.close();


    }

    public static void get() {
        OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        try {
            InputStream in = FileManager.get().open(ontoFile);
            try {
                ontoModel.read(in, "");
                ExtendedIterator<OntProperty> iterator1 = ontoModel.listAllOntProperties();
                ExtendedIterator<OntClass> iterator2 = ontoModel.listClasses();

                List<ExtendedIterator> list = new ArrayList<>();
                list.add(iterator1);
                list.add(iterator2);

                for (ExtendedIterator extendedIterator : list) {
                    while (extendedIterator.hasNext()) {
                        Object i = extendedIterator.next();
                        if (i.toString().contains("#")) {
                            System.out.println(i.toString().split("#")[1]);
                        }
                    }
                    System.out.println("----------------------");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JenaException je) {
            System.err.println("ERROR" + je.getMessage());
            je.printStackTrace();
            System.exit(0);
        }
    }

    public static String pretty(String s) {
        String[] list = s.split("#");
        String[] list2 = s.split("\\^");
        if (list2.length > 1) {
            return list2[0].replaceAll("_", " ");
        }

        if (list.length == 1) {
            return s.replaceAll("_", " ");
        } else {
            return list[1].replaceAll("_", " ");
        }

    }

    public static String upperFirstCharacter(String s) {
        String first = s.substring(0, 1);
        String remain = s.substring(1);
        return first.toUpperCase() + remain;
    }

    public static void main(String[] args) {
        try {
//            InitJena.insert("L???_h???i_?????n_?????c_??ng", "hasTimeHappen", "th??ng 2 ??m l???ch");
//
//            InitJena.insert2("L???_h???i_?????n_?????c_??ng", "hasOrganization", "Qu???ng_Ninh");
//            InitJena.insert2("L???_h???i_B???ch_?????ng", "hasOrganization", "Qu???ng_Ninh");
//            InitJena.insert("L???_h???i_?????n_?????c_??ng", Constant.PREFIX + "Festival");
//            InitJena.insert("L???_h???i_B???ch_?????ng", Constant.PREFIX + "Festival");
//
//            InitJena.insert2("isApartOf", "n???m trong");
//            InitJena.insert2("hasChronology", "c?? ni??n ?????i");
//            InitJena.insert2("hasOrganization", "???????c t??? ch???c t???i");
//            InitJena.insert2("hasDescription", "???????c m?? t??? l??");
//            InitJena.insert2("hasBuildTime", "???????c x??y d???ng v??o");
//            InitJena.insert2("wasDerivedFrom", "c?? ngu???n g???c t???");
//            InitJena.insert2("related", "c?? li??n quan");
//            InitJena.insert2("hasDied", "m???t v??o");
//            InitJena.insert2("hasBorn", "sinh v??o");
//            InitJena.insert2("orKnownAs", "hay c??n ???????c g???i l??");
//            InitJena.insert2("hasReignTo", "tr??? v??");
//            InitJena.insert2("hasTimeHappen", "di???n ra v??o");
//            InitJena.insert2("hasPeriod", "??? th???i k??? l??");
//            InitJena.insert2("hasBuildBy", "???????c x??y d???ng b???i");
//            InitJena.insert2("hasJob", "gi??? ch???c v???");
//            InitJena.insert2("hasSuccessor", "c?? ng?????i k??? v??? l??");
//            InitJena.insert2("hasBornAt", "sinh t???i");
//            InitJena.insert2("chosenCapitalBy","???????c ch???n l??m th??? ???? b???i");

//            InitJena.insert2("??i???n_Long_An","isApartOf","qu???n_th???_di_t??ch_C???_????_Hu???");
//            InitJena.insert("??i???n_Long_An",Constant.PREFIX+"CulturalHistoricalSite");
//            InitJena.insert("qu???n_th???_di_t??ch_C???_????_Hu???",Constant.PREFIX+"CulturalHistoricalSite");
//            InitJena.insert("????n_????_B??nh_??a","hasChronology","3000 n??m");
//            InitJena.insert("Kinh_th??nh_Hu???","hasBuildTime","n??m 1805");
//            InitJena.insert3("L???_h???i_?????n_?????c_??ng",Constant.PREFIX_TIME+"hasBeginning","3/2 ??m l???ch");
//            InitJena.insert3("L???_h???i_?????n_?????c_??ng",Constant.PREFIX_TIME+"hasEnd","cu???i th??ng 3 ??m l???ch");
//            InitJena.insert("????n_????_B??nh_??a",Constant.PREFIX+"ArchaeologicalHistoricalSite");
//            InitJena.insert("Kinh_th??nh_Hu???",Constant.PREFIX+"CitadelArchitecture");
//            InitJena.insert4(Constant.PREFIX_TIME+"hasBeginning","b???t ?????u v??o");
//            InitJena.insert4(Constant.PREFIX_TIME+"hasEnd","k???t th??c v??o");

//            InitJena.insert("Tr???n_Anh_T??ng",Constant.PREFIX+"Person");
//            InitJena.insert("Tr???n_Anh_T??ng","hasBorn","17-9-1276 (theo wikidata & dbpedia)");
//            InitJena.insert("Tr???n_Anh_T??ng","hasBorn","25-10-1276 (theo nguoikesu)");
//            InitJena.insert("pretty_people_data_8.json");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
