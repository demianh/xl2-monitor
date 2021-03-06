# XL2 Monitor
An app to read the screen of a NTi XL2 device through a serial connection and parse LAeq 60 decibel values from screen pixels.

The device needs to be connected to the computer through an usb cable. 

![screenshot](https://raw.githubusercontent.com/demianh/xl2-monitor/master/docs/screenshot.png)


## Download

[Download ZIP](https://raw.githubusercontent.com/demianh/xl2-monitor/master/xl2monitor.zip) or [Alternative Wide Build](https://raw.githubusercontent.com/demianh/xl2-monitor/master/xl2monitor-wide.zip)

Currently only OSX builds are available, though Windows should be possible. Requires X11 XQuartz to run.


## How it works

This screenshot shows which pixels are used to parse the number:

![screen cutouts](https://raw.githubusercontent.com/demianh/xl2-monitor/master/docs/example_screen_cutouts.png)

Result: double value 52.7


## Advanced / Debugging

You can run the binary directly from the commandline: `XL2Monitor.app/Contents/MacOS/monitor`

It allows to set two cli parameters: `monitor [debugging] [sync url]`

Example:

```
./monitor true http://localhost/api.php
```
