import time
import serial

# List ports on OSX:
# ls /dev/tty.*
# Windows: Query the Device Manager of your Windows PC to find out which COM port the
# system assigned to the XL2 and adapt the following line:
COM_PORT = "/dev/tty.usbmodem1411"

number_pattern = [
	'###     ######     ###',
	'    ###        ###    ',
	'      ####      ####  ',
	'    #####      ###### ',
	' ###  ###   ###  ###  ',
	'##############     ###',
	'########## ###########',
	'     ###        ###   ',
	'  #######   ######### ',
	'########### ##########']

xl2 = serial.Serial(COM_PORT, timeout=1)

#xl2.write('*RST\n'.encode()) # device reset
#xl2.write('ECHO Debugging Test\n'.encode())
#xl2.write('INIT START\n'.encode())
#xl2.write('SYST:KLOCK OFF\n'.encode()) # disable device key lock after reset

xl2.write('*IDN?\n'.encode()) # device info
print(xl2.readline())

xl2.write('RXL2S\n'.encode())
xl2.read()

byte = xl2.read(1)
colcount = 38
line = ''
lines = []
while byte != "":
	byte = xl2.read(1)
	if len(byte) == 0:
		break
	binary = "{0:b}".format(ord(byte)).zfill(8);
	bit1 = binary[:4]
	bit2 = binary[-4:]
	#print(bit1)
	#print(bit2)

	# replace bits with pixel representations
	if bit1 == "0010":
		line += '#'
	if bit1 == "0001":
		line += '.'
	if bit1 == "0000":
		line += ' '
	if bit1 == "0011":
		line += '*'

	if bit2 == "0010":
		line += '#'
	if bit2 == "0001":
		line += '.'
	if bit2 == "0000":
		line += ' '
	if bit2 == "0011":
		line += '*'

	colcount += 1

	if colcount == 80:
		colcount = 0
		lines.append(line)
		line = ''

# remove junk lines
del lines[0]
del lines[0]

# reverse screen lines because they are sent in reverse order
lines = lines[::-1]

# print screen
lc = 0
for li in lines:
	print(str(lc).zfill(3) + ':' + li)
	lc += 1

# parse LAeq60 numbers
cut1 = lines[100]
cut2 = lines[101]
print('\n')
print('Line 100:'+cut1)
print('Line 101:'+cut2)

# cut two lines of the number pixels out of the screen
number1 = cut1[76:-73]+cut2[76:-73]
number2 = cut1[89:-60]+cut2[89:-60]
number3 = cut1[102:-47]+cut2[102:-47]
number4 = cut1[120:-29]+cut2[120:-29]

print("--------")
print('1:'+number1)
print('2:'+number2)
print('3:'+number3)
print('4:'+number4)

db = ''

for x in range(0, 10):
	if number1 == number_pattern[x]:
		db += str(x)

for x in range(0, 10):
	if number2 == number_pattern[x]:
		db += str(x)

for x in range(0, 10):
	if number3 == number_pattern[x]:
		db += str(x)

db += '.'

for x in range(0, 10):
	if number4 == number_pattern[x]:
		db += str(x)

print('LAeq60 dB: ' + db)

xl2.write('INIT STOP\n'.encode())
xl2.close()
