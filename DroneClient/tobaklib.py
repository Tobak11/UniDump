import libardrone
from time import sleep

drone = libardrone.ARDrone() #ARDrone instance

def tTakeoff():
	drone.takeoff()
	print("Takeoff: Started")
	sleep(6)

	i=0
	j=0

	print("Takeoff: Moved")

	while j<4500:
		drone.hover()
		j+=1 

	print("Takeoff: Hovered")

def tLanding():
	i=0
	while i<10:
		drone.land()
		print("Landing")
		sleep(1)
		i+=1

def tHalt():
	drone.halt()
	print("Flight over")

def tHover(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Hovering")
		drone.hover()
		i+=1

def tTurnRight(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Turning right")
		drone.turn_right()
		i+=1

def tTurnLeft(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Turning left")
		drone.turn_left()
		i+=1

def tMoveForward(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Moving forward")
		drone.move_forward()
		i+=1

def tMoveBackward(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Moving backwards")
		drone.move_backward()
		i+=1

def tMoveUp(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Moving Up")
		drone.move_up()
		i+=1

def tMoveDown(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Moving Down")
		drone.move_down()
		i+=1

def tMoveLeft(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Moving Left")
		drone.move_left()
		i+=1

def tMoveRight(val):
	i=0
	while i<val:
		if i%2048==0:
			print("Moving Left")
		drone.move_right()
		i+=1

def tSetDroneSpeed(val):
	drone.set_speed(drone,val)