# Weather app

Your goal is to build a weather application which shows current weather conditions at user's location. The application has a bottom bar with 2 screens. 

## First screen 
The current weather conditions: temperature, pressure, humidity and wind. Also, a description of the condition and an icon representing it is shown. 

User can use pull to refresh to fetch the latest weather information. If pull to refresh fails, a snack bar message is shown with a retry option.

When a user enters the app and the API is not available or the user doesn't have internet enabled, a layout with warning message and retry option is shown.

If a user denies location permission, the app shows Zagreb as a default location with an indicator stating location is not available because permission is denied.

If a user location can not be found, the app shows an indicator with info message about it. 

## Second screen
A settings screen with 2 options 

* Unit of measurement - User can select between celsius, fahrenheit and kelvin. (default celsius). The first screen is refreshed when this option is changed.  
* Dark/Light mode switch - User can switch between dark and light mode for the first screen of the app. The screen should update accordingly.

## Other info
* The min support is Android 5.0 and the app can be locked in portrait mode. 
* The API reference can be found here https://openweathermap.org/api , but if you like, you can use any open weather API.
* The app should be compatible with Android 5.0+  devices. 
* You can use a programming language of your choice (Kotlin/Java). 
* You can use whatever third-party libs you like.
* We want to see how you think and solve problems. You can use some of the design patterns such as MVVM, MVP, Clean Architecture, but you’re not required to. Maybe you have some better architecture for a specific problem?
* The UX is also interesting to us, and we expect it to be usable.
* If you need icons, you can use your own, default or download them from here: https://material.io/icons/ 
