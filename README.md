CampusIR-MobileReportingPrototype
=================================

Project for CMSC434 - HCI, at University of Maryland, College Park.

Developed by Alexander Whipp
Designed by Enoch Hsiao and Matt Rzepka

##Overview and Activities
First and foremost this is a prototype. It will simulate all the stages of active use throughout its life-cycle.
###Login Activity
This activity is a standard username, and password interface. The username and password are blank to log in, with a remember me field to allow for easy return to the product.  Non-intrusive toasts are used throughout the product to simulate what is and is not possible. In this case it indicates when the information is incorrect.
###Menu Activity
2 of 3 tasks are reached from this screen. The first is submission of a new report. The second is viewing all reports. You can also log out from this screen.
###My Reports Activity
This is where the final task can be found. You can click on a report, and get more details (such as ETA or progress).
####Detailed View Subactivity
As aforementioned you can get numerous things from this third primary task. You can get the progress of your issue via a central progress bar, and you can also update the description section with new information. In a full model, this will allow time stamped information to be relayed to the facilities team in real time. Additionally, on their end they would also be able to respond. This is similar to a system DOTS currently has implemented at UMD.
###New Report Activity
This is your basic report screen. You fill in the appropriate information. The keyboard should come up just under all the required information. You can use GPS to pin point yourself in the nearest dorm on campus or click it manually if you are not at the location. Additionally, you can indicate the issue type and more specific location. If "other" is selected the use is mandated to fill out the description box (indicated by a toast), otherwise it is optional. Finally, the user is given the option to take a picture of the incident AND RETAKE the picture once it is captured. When submitting, a confirmation dialogue appears.
##Backend
This entire backend is created in order to provide the application on a singular device. There are no databases tied in with this. The only service you require for full local functionality is GPS.
#####MyApplication.java
This is the backbone of the program. It is the "global" variables used by all the activities. It contains a list of all the reports submitted.
#####GPSTracker.java
This is the class in charge of all the GPS related functionality.
#####Point.java
This is a small support class which allows hardcoded coordinates of dormitories, to be translated to distance from current location.
#####TitleBar.java
[DEPRECATED] old class which invoked a drawable for a customizable titlebar. Took up too much real estate, and did not provide intuitive navigation. Not native to android either.
#####Report.java
This is the serializable report class. It has all the information stored in each individual report. Further all of the information can be translated into a byte[] such that it can be stored in the application's systempreferences for future retrieval. This final step would not be needed (necessarily) if there was a backend database in charge of all the data. In fact, this requires the most storeage because images must be stored as bitmaps, and even as a byte[] these are relatively large files.
