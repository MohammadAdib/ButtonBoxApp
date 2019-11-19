# ButtonBox - For Sim Racing & Games

ButtonBox is an app for gamers to control their sim or game experience through the phone by simulating keyboard inputs.

Download the server here: https://github.com/MohammadAdib/ButtonBoxServer

![](https://i.imgur.com/CtKS1tB.png)

### Default key bindings
- A/B/X/Y are mapped to their respective keys (pressing them is the same as pressing those keys on your keyboard)
- The red +/- buttons are mapped to Q and W respectively
- The yellow +/- buttons are mapped to E and R respectively
- The blue +/- buttons are mapped to T and Y respectively

Long pressing on the button allows you to choose an new key binding. Key bindings are unique, meaning you cannot have more than one button bind to a key

### How it works
ButtonBox uses UDP to connect the devices together. The app has a built in repetitive 10 second heartbeat to check for connectivity with servers. Auto-discovery is possible as both the app and server have a UDP client and server passing ack's back and forth. 

For simulating key presses, a java Robot is used.

### Future development
Future plans to expand this might include integration with real-time telemetry from games like Assetto Corsa
