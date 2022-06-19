# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/custom-actions


# This is a simple example for a custom action which utters "Hello World!"

# from typing import Any, Text, Dict, List

# from rasa_sdk import Action, Tracker
# from rasa_sdk.executor import CollectingDispatcher


# class ActionHelloWorld(Action):

#     def name(self) -> Text:
#         return "action_hello_world"

#     def run(self, dispatcher: CollectingDispatcher,
#             tracker: Tracker,
#             domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

#         dispatcher.utter_message(text="Hello World!")

#         return []
from typing import Any, Iterator, Text, Dict, List

from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
import random
import requests


DATABASE = ["bún đậu mắm tôm",
            "bún đậu nước mắm",
            "bún cá",
            "bún hải sản",
            "cơm văn phòng",
            "cơm sườn",
            "xôi",
            "bún ốc",
            "mì vằn thắn",
            "hủ tiếu",
            "bún chả",
            "bún ngan",
            "ngan xào tỏi",
            "bún bò huế",
            "mì tôm hải sản",
            "bánh mì trứng xúc xích rắc thêm ít ngải cứu",
            "bánh mì trứng",
            "bánh mì xúc xích",
            "bánh mì pate"]


class ActionRecommend(Action):

    def name(self) -> Text:
        return "action_recommend"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        food = []
        for i in range(2):
            food_number = random.randrange(len(DATABASE))
            food.append(DATABASE[food_number])

        dispatcher.utter_message(
            text="Em nghĩ hôm nay anh chị có thể thử món '{}' hoặc bên cạnh đó cũng có thể là món '{}' ạ".format(food[0], food[1]))

        return []

# class ActionAskPerson(Action):

#     def name(self) -> Text:
#         return "action_ask_person"

#     def run(self, dispatcher: CollectingDispatcher,
#             tracker: Tracker,
#             domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:      
        
#         input_data = next(tracker.get_latest_entity_values("citadel"), None)
#         print(input_data)
#         r = requests.get("http://localhost:8080/chosenCapitalBy",data=input_data)      

#         dispatcher.utter_message(
#             text="Cổ Loa được chọn làm thủ đô dưới thời vua {}.".format(r.content.decode()))

#         return []

# class ActionAskFestival(Action):

#     def name(self) -> Text:
#         return "action_ask_festival"

#     def run(self, dispatcher: CollectingDispatcher,
#             tracker: Tracker,
#             domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        
#         input_data1 = next(tracker.get_latest_entity_values("X"), None)
#         input_data2 = next(tracker.get_latest_entity_values("object"), None)
#         input_data = input_data1 + "~"+ input_data2
#         r = requests.get("http://localhost:8080/festival",data=input_data.encode('utf-8'))
       
#         print(input_data)
#         dispatcher.utter_message(
#             text="Đó là những "+input_data1+" như {}.".format(r.content.decode()))

#         return []

# class ActionAskFestival2(Action):

#     def name(self) -> Text:
#         return "action_ask_festival_2"

#     def run(self, dispatcher: CollectingDispatcher,
#             tracker: Tracker,
#             domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
#         input_data1 = next(tracker.get_latest_entity_values("X"), None)
#         iterator = tracker.get_latest_entity_values("object")
#         input_data2 = next(iterator, None)
#         input_data3 = next(iterator, None)

#         input_data = input_data1 + "~"+ input_data2 +"~"+input_data3
#         r = requests.get("http://localhost:8080/festival2",data=input_data.encode('utf-8'))
       
#         print(input_data)
#         dispatcher.utter_message(
#             text="Đó là những "+input_data1+" như {}.".format(r.content.decode()))

#         return []        
class ActionOneCondition(Action):
    def name(self) -> Text:
        return "action_one_condition"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        input_data1 = next(tracker.get_latest_entity_values("X"), None)
        input_data2 = next(tracker.get_latest_entity_values("predicate"), None)
        input_data3 = next(tracker.get_latest_entity_values("object"), None)
       

        input_data = input_data1 + "~"+ input_data2 +"~"+input_data3
        r = requests.get("http://localhost:8080/one_condition",data=input_data.encode('utf-8'))
       
        print(input_data)
        dispatcher.utter_message(
            text="{}".format(r.content.decode()))

        return []    
class ActionTwoConditions(Action):
    def name(self) -> Text:
        return "action_two_conditions"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        input_data1 = next(tracker.get_latest_entity_values("X"), None)
        iterator1 = tracker.get_latest_entity_values("predicate")
        iterator2 = tracker.get_latest_entity_values("object")
        input_data2 = next(iterator1,None)
        input_data3 = next(iterator2,None)
        input_data4 = next(iterator1,None)
        input_data5 = next(iterator2,None)
       

        input_data = input_data1 + "~"+ input_data2 +"~"+input_data3+"~"+input_data4+"~"+input_data5
        r = requests.get("http://localhost:8080/two_conditions",data=input_data.encode('utf-8'))
       
        print(input_data)
        dispatcher.utter_message(
            text="{}".format(r.content.decode()))

        return []           

class ActionAskOneProperty(Action):
    def name(self) -> Text:
        return  "action_ask_one_property"


    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:
        input_data1 = next(tracker.get_latest_entity_values("object"), None)
        input_data2 = next(tracker.get_latest_entity_values("predicate"),None)

       

        input_data = input_data1 + "~"+ input_data2 
        r = requests.get("http://localhost:8080/ask_one_property",data=input_data.encode('utf-8'))
       
        print(input_data)
        dispatcher.utter_message(
            text="{}".format(r.content.decode()))

        return []          