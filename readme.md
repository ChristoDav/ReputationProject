## What is ReputationProject
An assistant desktop application to help world builders keep track of all the moving parts.
When expanding a world, it's important to remember what's already there to avoid conflicts or discrepancies in the future.
This application should centralise as much of that information as possible to help make that easier, while also making it possible to develop the world purely within the app.<br>

## Why "ReputationProject"
ReputationProject began as a console-based Java project in 2022 as a way to reinforce my understanding of the language and practice introducing data serialisation.
It's original scope was to simply keep track of the "reputation" an actor would have of other actors in the world, with the adding of "interactions" between actors that carried a reputation value changes and a description of why the change occured. <br>
I wanted to develop my understanding of full-stack development, and as such, it feels fitting to pick up ReputationProject again, but with an expanded scope.

## Planned Features:
#1 A starting "World Menu" for starting a new world, opening an existing world, and exiting the application. <br>
#2 Home page containing the project title, and a display of central application functions along with additional functionality (showing recently accessed pages etc). <br>
#3 A save state that is automatically read on startup from a SQLite database. <br>
#4 A sliding side menu for accessing app functions such as returning to the home page, opening other app functionality pages i.e. actors page, opening a settings menu, returning the starting world menu, and exiting the program. <br>
#5 A settings menu accessable from the sliding side menu that allows for a choice between importing or exporting the current database contents, changing between 3 different resolution presets (small, medium, big), a dark and light mode, and wiping the current database with extra confirmation when doing so. <br>
#6 An actor page allowing for the creation of new actors (containing names, descriptions, including whether or not they are are player controlled, in which case they also require the name of the player controlling them), an option to add preset (friend, enemy, sibling, parent etc) or custom relationships between other existing actors, the ability to edit existing actors details and relationships, and the ability to delete actors. <br>
#7 An interaction page for defining interactions between actors containing a description of what happened, a value change in reputation, andd a date of when it occured. <br>
#8 A world page for defining the world, including the name, description, countries with an additional option to include details for the country, and locations. The nationality or place of birth details of an actor in the actor menu should allow for a drop down containing the countries and/or locations made here, with a bonus option for a custom input. <br>
#9 An event page for defining events. These can either relate to specific actors or the world itself; what happened, who was involved, are any interactions tied to this event, when did it happen.<br>
#10 A timeline page, showcasing when actors were born, when they died (if dead), and when events and interactions occurred in a timeline.