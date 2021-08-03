Scheduling Application

Author: Sarah Berg
Application Version 1.0 
Date:7/18/2021

The purpose is to develop a scheduling desktop application for a multilingual global consulting organization using the following skills:
•	database and file server applications
•	lambda expressions
•	collections
•	localization
•	exception handling 

Dependencies:
•	IDE & SDK Versions:
•	IntelliJ IDEA Community Edition 2021.1
•	Java SE 11.0.8
•	JavaFX-SDK-11.0.2
•	MySQL-connector-java-8.0.23

Directions:
•	Open the application in the IDE 
•	Install MySQL connector listed above
•	For Intellij: Go into the file menu >Project Structure > Global Libraries. From there add JavaFx to the project. 

	Login:
	o Username: test
	o Password: test


Notes:

• 	Adding, updating, and deleting appointments can be accessed via the appointments button in the main menu.
• 	Customers can be added, updated, and deleted from the Customer Records page. 

The appointment types are hardcoded and consist of the following options:
•	De-Briefing,
•	Planning Session,
•	Lunch Meeting,
•	Other

If this list interferes with the testing data, they are in the AddAppointment class on line 367, and on line 390 in the UpdateAppointment class.

Project Requirement A3f:
The report of my choosing tracks and displays each consultant's total number of appointments in a specified time frame.
This is useful for assessing employee performance.
