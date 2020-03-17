## Tasmota (Connect)
Tasmota (Connect) is a SmartApp & Device Type for Hubitat Elevation, that allows you to add your Pure Tasmota [Tasmota](https://github.com/arendst/Tasmota) devices as Hubitat devices.

## Features
* Real-time device status
* Pure Tasmota & HE integration (No need for additional MQTT bridge)
* Virtual Device (for RF / IR devices that can be controlled by a RF / IR bridge) 

## Requirement
* Hubitat Elevation Hub
* Tasmota >=8.10 (recommended)

## Installation


#### Manual Installation
For Tasmota (Connect) to function correctly, please make sure you install the SmartApp and all Device Handlers.

## Adding your Tasmota devices
1. Under `Apps`, select ADD USER APP
2. Select `Tasmota (Connect)`
2. Tap `New Tasmota Device`, and select the Tasmota device you want to add
3. Fill in the `IP address`, `username` (optional), `password` (optional) of the Tasmota device


## Supported Tasmota Devices

It should work for most switches, dimmers, relays, plugs, power strips, sockets, wall outlets, fan controllers, IR bridges and RF bridges listed in the [Tasmota Device Templates Repository](https://templates.blakadder.com/).

If your Tasmota device is not listed below, choose a **Generic** device that is similar to your Tasmota device.

* **Generic Switch** (1,2,3,4,5,6CH) - No Power Monitoring
* **Generic Metering Switch** (1,2CH) - Power Monitoring
* **Generic Dimmer Switch**
* **Generic IR Bridge**
* Sonoff Basic
* Sonoff RF
* Sonoff TH (On/Off only)
* Sonoff Dual & Dual R2
* Sonoff Pow & Pow R2
* Sonoff 4CH & 4CH Pro
* Sonoff S20, S26
* Sonoff S31
* Sonoff Touch, T1 (1,2,3CH)
* Sonoff RF Bridge
* Sonoff iFan02, iFan03

## Virtual Device
A Virtual Device uses a RF or IR bridge to control your dumb RF / IR devices.
* Virtual Switch -- Add a SmartThings generic switch that can be controlled by a RF / IR bridge (e.g. Sonoff RF Bridge).
* Virtual Shade  -- Add a SmartThings shade/blind that can be controlled by a RF / IR bridge (e.g. Sonoff RF Bridge).
* Virtual Button -- Add RF/IR remote controller 1/2/4/6-button as SmartThings remote controller button.
  
#### Configuring a Virtual Switch
1. Choose a RF or IR Bridge
2. Enter the Tasmota command to send for "ON" and "OFF"
      
      For RF, it has to be one of these formats
      
      * `Backlog RfSync <value>; RfLow <value>; RfHigh <value>; RfCode <value>`      
      * `Backlog RfRaw <value>; RfRaw 0`
         > Note: RFRaw requires [Portisch Firmware](https://github.com/Portisch/RF-Bridge-EFM8BB1)
     
3. Optionally, enable `State tracking` to listen for RF codes (e.g. from RF remote) to simulate a Stateful device
4. If you enable `State tracking`, enter the code that represents the "ON" and "OFF" state
       
   In the example below, the code is **70C70F**
   
   * "RfReceived":{"Sync":7110,"Low":210,"High":660,"Data":"**70C70F**","RfKey":"None"}

## FAQ

#### My Tasmota device is not responding?
* Verify the IP address is correct and you are able to access your device via the IP address.
* If you have added this device using other developer device type handler (DTH), please delete the device.
* If the device is still not responding, look at the Tasmota Console so that you can see exactly what is happening. e.g. The actions (on/off) that you've called in SmartThings App didn't appear in the Console, it's likely the SmartThings hub is unable to access the device.
* Ensure your Tasmota devices and SmartThings hub are assigned with static IP addresses.

####  Where can I find out more about Tasmota command for sending/receiving IR / RF codes?
Please see under RF Bridge & IR Remote - https://tasmota.github.io/docs/#/Commands

#### I have feedback, questions, suggestion..
Please use [SmartThings Community](https://community.smartthings.com/t/release-tasmota-connect-pure-tasmota-st-integration-real-time-status-for-sonoff-tuya-smartlife-other-esp8266-devices/187553) for feedback and questions.

## License
GPL-3.0
