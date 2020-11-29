/**
 *  Tasmota - Generic Switch
 *
 *  Copyright 2020 AwfullySmart.com - HongTat Tan
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 04/05/2020 - Modified by PabloGux to run with Hubitat Elevation 
 *  - Added initialize capability
 *  - Added decode() and encode() methods
 *  - Updated initialize() to encode Tasmota rules URL parameters
 *  - Updated parse() to decode message.header (HTTP response header)
 */ 

metadata {
    definition(name: "Tasmota Generic Switch", namespace: "hongtat", author: "HongTat Tan", ocfDeviceType: "oic.d.switch") {
        capability "Actuator"
        capability "Health Check"
        capability "Switch"
        capability "Polling"
        capability "Refresh"
        capability "Sensor"
        capability "Initialize" 
        capability "Signal Strength"

        attribute "lastSeen", "string"
        attribute "version", "string"
    }

    simulator {
    }

    preferences {
        section {
            input(title: "Device Settings",
                    description: "To view/update this settings, go to the Tasmota (Connect) SmartApp and select this device.",
                    displayDuringSetup: false,
                    type: "paragraph",
                    element: "paragraph")
        }
    }

    tiles(scale: 2) {
        multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#00A0DC"
                attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff"
            }
        }

        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
        }
        standardTile("lqi", "device.lqi", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label: 'LQI: ${currentValue}'
        }
        standardTile("rssi", "device.rssi", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label: 'RSSI: ${currentValue}dBm'
        }

        main "switch"
        details(["switch", "refresh", "lqi", "rssi"])
    }
}




def installed() {
    sendEvent(name: "checkInterval", value: 30 * 60 + 2 * 60, displayed: false, data: [protocol: "lan", hubHardwareId: device.hub.hardwareID])
    sendEvent(name: "switch", value: "off")
    log.debug "Installed()"
    response(refresh())
}

def uninstalled() {
    sendEvent(name: "epEvent", value: "delete all", isStateChange: true, displayed: false, descriptionText: "Delete endpoint devices")
}

def updated() {
    initialize()
}

def initialize() {
    log.debug  ("initialize()")    
    if (device.hub == null) {
        log.error "Hub is null, must set the hub in the device settings so we can get local hub IP and port"
        return
    }

    def syncFrequency = (parent.generalSetting("frequency") ?: 'Every 1 minute').replace('Every ', 'Every').replace(' minute', 'Minute').replace(' hour', 'Hour')
    try {
        "run$syncFrequency"(refresh)
    } catch (all) { }

    parent.callTasmota(this, "Status 5")
   //  s = s.replace('X1','?').replace('X2','=').replace('X3','{').replace('X6',':').replace('X4',"\\").replace('X5','"').replace('X7','}')
    def pwr="/?json={\"StatusSTS\":{\"POWER\":\"%value%\"}}"
    def pwr1= "/?json={\"StatusSTS\":{\"POWER1\":\"%value%\"}}"
    def pwr2= "/?json={\"StatusSTS\":{\"POWER2\":\"%value%\"}}"
    def pwr3= "/?json={\"StatusSTS\":{\"POWER3\":\"%value%\"}}"
    def pwr4= "/?json={\"StatusSTS\":{\"POWER4\":\"%value%\"}}"
    def pwr5= "/?json={\"StatusSTS\":{\"POWER5\":\"%value%\"}}"
    def pwr6= "/?json={\"StatusSTS\":{\"POWER6\":\"%value%\"}}"
    
    pwr=encode(pwr)
    pwr1=encode(pwr1)
    pwr2=encode(pwr2)
    pwr3=encode(pwr3)
    pwr4=encode(pwr4)
    pwr5=encode(pwr5)
    pwr6=encode(pwr6)

    
    //parent.callTasmota(this, "Backlog Rule1 ON Power#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER\":\"%value%\"}} ENDON ON Power1#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER1\":\"%value%\"}} ENDON ON Power2#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER2\":\"%value%\"}} ENDON ON Power3#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER3\":\"%value%\"}} ENDON ON Power4#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER4\":\"%value%\"}} ENDON;Rule1 1")
    //parent.callTasmota(this, "Backlog Rule2 ON Power5#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER5\":\"%value%\"}} ENDON ON Power6#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER6\":\"%value%\"}} ENDON;Rule2 1")
    parent.callTasmota(this, "Backlog Rule1 ON Power#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr + " ENDON ON Power1#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr1 +" ENDON ON Power2#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr2 +" ENDON ON Power3#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr3 +" ENDON; Rule1 1")                                                                                                                                                                 
    //parent.callTasmota(this, "Backlog Rule2 ON Power5#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER5\":\"%value%\"}} ENDON ON Power6#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER6\":\"%value%\"}} ENDON;Rule2 1")
    parent.callTasmota(this, "Status 8")
    log.debug  ("initialize() exit")    
    refresh()
}

def initializeIpAddress() {
    log.debug  ("initializeIpAddress()")    
    if (device.hub == null) {
        log.error "Hub is null, must set the hub in the device settings so we can get local hub IP and port"
        return
    }

    parent.callTasmota(this, "Status 5")

    def pwr="/?json={\"StatusSTS\":{\"POWER\":\"%value%\"}}"
    def pwr1= "/?json={\"StatusSTS\":{\"POWER1\":\"%value%\"}}"
    def pwr2= "/?json={\"StatusSTS\":{\"POWER2\":\"%value%\"}}"
    def pwr3= "/?json={\"StatusSTS\":{\"POWER3\":\"%value%\"}}"
    def pwr4= "/?json={\"StatusSTS\":{\"POWER4\":\"%value%\"}}"
    def pwr5= "/?json={\"StatusSTS\":{\"POWER5\":\"%value%\"}}"
    def pwr6= "/?json={\"StatusSTS\":{\"POWER6\":\"%value%\"}}"
    
    pwr=encode(pwr)
    pwr1=encode(pwr1)
    pwr2=encode(pwr2)
    pwr3=encode(pwr3)
    pwr4=encode(pwr4)
    pwr5=encode(pwr5)
    pwr6=encode(pwr6)

    
    //parent.callTasmota(this, "Backlog Rule1 ON Power#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER\":\"%value%\"}} ENDON ON Power1#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER1\":\"%value%\"}} ENDON ON Power2#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER2\":\"%value%\"}} ENDON ON Power3#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER3\":\"%value%\"}} ENDON ON Power4#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER4\":\"%value%\"}} ENDON;Rule1 1")
    //parent.callTasmota(this, "Backlog Rule2 ON Power5#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER5\":\"%value%\"}} ENDON ON Power6#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER6\":\"%value%\"}} ENDON;Rule2 1")
    parent.callTasmota(this, "Backlog Rule1 ON Power#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr + " ENDON ON Power1#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr1 +" ENDON ON Power2#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr2 +" ENDON ON Power3#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] " + pwr3 +" ENDON; Rule1 1")                                                                                                                                                                 
    //parent.callTasmota(this, "Backlog Rule2 ON Power5#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER5\":\"%value%\"}} ENDON ON Power6#state DO WebSend ["+device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")+"] /?json={\"StatusSTS\":{\"POWER6\":\"%value%\"}} ENDON;Rule2 1")
    parent.callTasmota(this, "Status 8")
    log.debug  ("initialize() exit")  
    
    refresh()
}

def encode(String message) {  
    def s  = message
    s = s.replace('?','X1').replace('=','X2').replace('{','X3').replace(':','X6').replace('\\',"").replace('"','X5').replace('}','X7')
    return s
 } 

def decode(String message) {   
    def s  = message
    s = s.replace('X1','?').replace('X2','=').replace('X3','{').replace('X6',':').replace('X4',"\\").replace('X5','"').replace('X7','}')
    return s
 }
 
def parse(String description) { 
    def events = null
    def message = parseLanMessage(description)
    log.debug ("parse(): message=" + message)
    log.debug ("parse(): message.header =" + message.header) 
    def s =  message.header
    log.debug ("escaped message=" + s)
    s = decode(s)
    log.debug ("parse(): clean message =" + s)
    
   
    def json = parent.getJson(s)
    if (json != null) {
        events = parseEvents(200, json)
    }
    return events
}

def calledBackHandler(hubitat.device.HubResponse hubResponse) {
    log.debug ("calledBackHandler(): Device is receiving a message") 
    def events = null
    def status = hubResponse.status
    def json = hubResponse.json
    log.debug ("calledBackHandler(): calling parseEvents() with params status=" + status + " json=" + json)
    events = parseEvents(status, json)
    return events
}

def parseEvents(status, json) {
     log.debug ("parseEvents(): status= " + status + " , json=" + json)
    def events = []
    if (status as Integer == 200) {
        def channel = getDataValue("endpoints")?.toInteger()
        def eventdateformat = parent.generalSetting("dateformat")
        def now = new Date().format("${eventdateformat}a", location.timeZone)

        // Power
        if (channel != null) {
            log.debug ("parseEvents() Power event - it's a power change event")
            for (i in 0..channel) {
                def number = (i > 0) ? i : ""
                def power = (json?.StatusSTS?."POWER${number}" != null) ? (json?.StatusSTS?."POWER${number}") : ((json?."POWER${number}" != null) ? json?."POWER${number}" : null)

                def powerStatus = null
                if (power in ["ON", "1"]) {
                    powerStatus = "on"
                    log.debug ("parseEvents() Power event - power on")
                } else if (power in ["OFF", "0"]) {
                    powerStatus = "off"
                    log.debug ("parseEvents() Power event - power off")
                }
                if (powerStatus != null) {
                    if ((channel == 1) || (channel > 1 && i == 1)) {
                        events << sendEvent(name: "switch", value: powerStatus)
                    } else {
                        String childDni = "${device.deviceNetworkId}-ep$i"
                        def child = childDevices.find { it.deviceNetworkId == childDni }
                        child?.sendEvent(name: "switch", value: powerStatus)
                    }
                }
            }
        }

        // Temperature, Humidity (Status 8)
        def resultTH = null
        if (json?.StatusSNS != null) {
            for (record in json.StatusSNS) {
                if (record.value instanceof Map && (record.value.containsKey("Humidity") || record.value.containsKey("Temperature"))) {
                    resultTH = record.value
                }
            }
            if (resultTH != null) {
                def childMessage = [:]
                if (resultTH.containsKey("Humidity")) {
                    childMessage.humidity = Math.round((resultTH.Humidity as Double) * 100) / 100
                }
                if (resultTH.containsKey("Temperature")) {
                    childMessage.temperature = resultTH.Temperature.toFloat()
                }
                if (json?.StatusSNS?.TempUnit != null) {
                    childMessage.tempUnit = json?.StatusSNS?.TempUnit
                }
                def child = childDevices.find { it.typeName == "Tasmota Child Temp/Humidity Sensor" }
                child?.parseEvents(200, childMessage)
            }
        }

        // MAC
        if (json?.StatusNET?.Mac != null) {
            def dni = parent.setNetworkAddress(json.StatusNET.Mac)
            def actualDeviceNetworkId = device.deviceNetworkId
            if (actualDeviceNetworkId != state.dni) {
                runIn(10, refresh)
            }
            log.debug "MAC: '${json.StatusNET.Mac}', DNI: '${state.dni}'"
            if (state.dni == null || state.dni == "" || dni != state.dni) {
                if (channel > 1 && childDevices) {
                    childDevices.each {
                        it.deviceNetworkId = "${dni}-ep" + parent.channelNumber(it.deviceNetworkId)
                        log.debug "Child: " + "${dni}-ep" + parent.channelNumber(it.deviceNetworkId)
                    }
                }
            }
            state.dni = dni
        }

        // Signal Strength
        if (json?.StatusSTS?.Wifi != null) {
            events << sendEvent(name: "lqi", value: json?.StatusSTS?.Wifi.RSSI, displayed: false)
            events << sendEvent(name: "rssi", value: json?.StatusSTS?.Wifi.Signal, displayed: false)
        }

        // Version
        if (json?.StatusFWR?.Version != null) {
            state.lastCheckedVersion = new Date().getTime()
            events << sendEvent(name: "version", value: json.StatusFWR.Version, displayed: false)
        }

        // Call back
        if (json?.cb != null) {
            parent.callTasmota(this, json.cb)
        }

        // Last seen
        events << sendEvent(name: "lastSeen", value: now, displayed: false)
    }
    return events
}

def on() {
    def channel = getDataValue("endpoints")?.toInteger()
    parent.callTasmota(this, "POWER" + ((channel == 1) ? "" : 1) + " 1")
}

def off() {
    def channel = getDataValue("endpoints")?.toInteger()
    parent.callTasmota(this, "POWER" + ((channel == 1) ? "" : 1) + " 0")
}

def refresh(dni=null) {
    log.debug ("refresh(): IP: " + it.ip)
    if (!it.ip)
    {
        initializeIpAddress()
    } 
    def lastRefreshed = state.lastRefreshed
    if (lastRefreshed && (now() - lastRefreshed < 5000)) return
    state.lastRefreshed = now()

    // Check version every 30m
    def lastCheckedVersion = state.lastCheckedVersion
    if (!lastCheckedVersion || (lastCheckedVersion && (now() - lastCheckedVersion > (30 * 60 * 1000)))) {
        parent.callTasmota(this, "Status 2")
    }

    def actualDeviceNetworkId = device.deviceNetworkId
    if (state.dni == null || state.dni == "" || actualDeviceNetworkId != state.dni) {
        parent.callTasmota(this, "Status 5")
    }
    parent.callTasmota(this, "Status 8")
    parent.callTasmota(this, "Status 11")
}

def ping() {
    refresh()

}

def childOn(dni) {
    parent.callTasmota(this, "POWER" + parent.channelNumber(dni) + " 1")
}

def childOff(dni) {
    parent.callTasmota(this, "POWER" + parent.channelNumber(dni) + " 0")
}
