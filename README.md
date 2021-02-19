Alarm US
This project aims to be a fun wakeup alarm for the family themed around the popular Among US game.



Screens:
 - Infoscreen:
   - Alarm element
   - Shows tasklist
   ?allowToggleTask
   ?showTaskList
   ?showTaskProgress
   ?flashAlarm

 - Mobile screen
   - Touchpad for log-in
   - File transfer screen


Game setup:
 - Timeout (Given as an amount of time after start or a time of day)
 - 
 - Critical tasks (Must be solved to stop initial alarm)
 - Other tasks (Must be done before timeout)

Players:
 - Each player has a set of tasks to complete
 


Tasks:
 - Login (at least once)
 - Transfer files
 
 - Toggle tasks:
   - Pusse tenna
   - Bøker i sekken
   - Pakke matboks
   - Pakke drikkeflaske
   - 


Game stages:
- Lobby: Starting with calm music (Soft wake up)
  - Calls a set of urls to set up home automation for "good morning"
- Game start: Alarm sound and red blinking lights (home automation integration)
  - Calls a set of urls to set up home automation for "alarm mode"
  
  - During the alarm loop:
    - Call a set of urls to set alarm light scene high
	- Play a sound.
	- Wait a given amount of seconds
	- Call a set of urls to set alarm light scene low
	- Wait a given amount of seconds

  - Alarm mode turns off when critical tasks are done.

  - When alarm is done, call set of URLs to set up home automation for "day is here"

- Timer fortsetter å telle
  - 5 mins before timeout

- At timeout:
  - If all tasks done, show victory screen.
  - Else: Defeat screen.


Architecture:




Vistory screen: https://github.com/theamanjs/among-us

Among Us figures: https://seeklogo.com/vector-logo/387055/among-us