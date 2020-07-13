Original App Design Project - README Template
===

# New Routes

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that randomly generates running routes using google maps or mapbox.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Fitness
- **Mobile:** Easy to use while on a run or starting a run outside
- **Story:** Allows runners to try different routes every run
- **Market:** Anyone who jogs outside.
- **Habit:** People who jog tend to do it frequently
- **Scope:** Still not sure how the app can be implemented, using google maps or mapbox. implementing it with mapbox seems to be easier, but there's still the issue of actually generating random routes that are valid. The first idea i thought of was just choosing a point to define the center of your route, and trying to get the closest roads to match that path. alternatively, I could try choosing a set # of points on the circle, setting them as waypoints, and having the api navigate to each waypoint. Both ideas have the issue of not being able to validate the points that we choose, I don't think theres any way to avoid trying to gnerate paths in water or woods or other invalid areas, so error handling is going to be a thing.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can signup and login
* Users can add a profile picture
* Users can use google maps or mapbox to view a map in the app
* Users can generate a running route close to a set length that starts and ends at a specific location on the map. 


**Optional Nice-to-have Stories**

* Users can save routes
* Users can friend other users and share routes
* Users can like or dislike routes they get
* Users can customize routes more, for example specifying an endpoint instead of ending at the start

### 2. Screen Archetypes

* Signup/Login screen
   * Users can signup/login
* Profile screen
   * Users can add a profile picture
   * Users can view a map in app
   * Users can generate new routes
   * Users can view their saved routes
* Timeline
    * Users can view the routes that other users generated
    * Users can friend other users

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Profile Screen
* Timeline

**Flow Navigation** (Screen to Screen)

* Login
   * -> Profile
* Profile
   * -> Timleine

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://imgur.com/a/1avxA8B" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models

#### Route
| Property | Type | Description |
| -------- | -------- | -------- |
| Name     | String     | Name of the route|
|Length | Int| Length of the route|
| Waypoints|ArrayList<Coordinates>|A list of the waypoints for the route|

#### Coordinate


| Column 1 | Column 2 | Column 3 |
| -------- | -------- | -------- |
| Long     | Int     | Longitude Coord    |
| Lat     | Int     | Latitude Coord    |


### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
