# **MyNearby**, a "Meet Your Surroundings" Demo App

## **1. Introduction**

Developed over the course of a week, **MyNearby** is an Android navegation demo app that shows noteworthy **points of interest (POIs)** around your current location, incentivizing **exploration** and offering **quick feedback** on what could be interesting to get to know about your surroundings.

This application was built with the **Android SDK**, scritped in **Kotlin** and using the **Google Maps API**'s tools, with the intent of being a learning project for me, who had never utilized any of these before in my coding journey.

#

## **2. Functionalities**

With the aim of providing a **seamless and useful** experience to the users, I developed the funtionalities I saw fit for **what the application needed** and that **would be helpful** when navigating the map.

#

### **2.1 Create New Place**

Should the user want or need to **create a new marker**, to, for example, in a more developed version of the application, share with other users who want to explore community catered locations, they can create a new marker by:

- Clicking a non-occupied location on the map;

- Filling both input fields in the pop-up window;

- Completing the task by clicking the "Create New Place" button.

The marker will then appear on the map and can be pressed in order to check the information input by the user in the **info window**.

<table style="margin-left: auto; margin-right: auto;">
    <tr>
        <td><img src="docs/create_new_place.gif" width="235"></td>
        <td><img src="docs/create_new_place_error.gif" width="235"></td>
        <td><img src="docs/create_new_place_check.gif" width="235"></td> 
    </tr>
    <tr>
        <td><b>2.1.1</b> Creating new place</td>
        <td><b>2.1.2</b> Failed to fill both fields</td>
        <td><b>2.1.3</b> Checking created place</td>
    </tr>
</table>

#

### **2.2 Create New Place**

If the user desires to **explore a place near their location** that is already in the **Google Maps** database, they can **click** one of the **pre-disposed POI markers** to prompt a pop-up displaying pertinent information about that place, such as:

- Location name;

- Location type (Restaurant, Bus Station, etc.);

- User ratings and rating amount;

- Establishment price assessment;

- Location address;

- Delivery/Dining options;

- Location Status (Open, Closed, Permanently Closed);

- Establishment schedule for the current weekday;

- Carousel of the location's photos. 

The pop-up window's layout also depends on the information in the database, thus only displaying the existing information in an ordered and convenient manor.

<table style="margin-left: auto; margin-right: auto;">
    <tr>
        <td><img src="docs/check_place_example_1.gif" width="200"></td>
        <td><img src="docs/check_place_example_2.gif" width="200"></td>
        <td><img src="docs/check_place_example_3.gif" width="200"></td> 
        <td><img src="docs/check_place_example_4.gif" width="200"></td> 
    </tr>
    <tr>
        <td><b>2.2.1</b> Fully Detailed</td>
        <td><b>2.2.2</b> Few Details</td>
        <td><b>2.2.3</b> Temporarily Closed</td>
        <td><b>2.2.4</b> Bus Stop</td>
    </tr>
</table>

#

### **2.3 Return to Current Location**

In the event of the user losing track of their current position, they can **press the button** on the **bottom right corner** to quickly return to it.

<table style="margin-left: auto; margin-right: auto;">
    <tr>
        <td><img src="docs/return_to_location.gif" width="250"></td>
    </tr>
    <tr>
        <td><b>2.3.1</b> Go to Current Location</td>
    </tr>
</table>

#

## **3. Case Study**

As previously mentioned, this being my first time developing an Android app, a lot had to be **researched and learned** before I could fully develop a functioning application with the features I had invisioned. Among others, these are some of the concepts and skills I can recognize I am more confortable working with following this learning experience:

- Programming in Kotlin;

- Android Studio's file structure;

- Android's Activity components;
  
- Anroid's layout design (Different layouts, View Elements, Toast, Dialogs, etc.)

- Permission requests and handling;
  
- Google Maps API and other associated API's (Google Maps Places).