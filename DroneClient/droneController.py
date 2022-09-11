import tobaklib
from time import sleep

file = open("PyInput.txt", "r")

inputArray = file.read().splitlines()

if len(inputArray) != 0:
    tobaklib.tTakeoff()

    for param in inputArray:
        controlValue = int(param.split(":")[1]) * 5000
        if param.split(":")[0] == "Up":
            tobaklib.tMoveUp(controlValue)
        elif param.split(":")[0] == "Down":
            tobaklib.tMoveDown(controlValue)
        elif param.split(":")[0] == "Left":
            tobaklib.tMoveLeft(controlValue)
        elif param.split(":")[0] == "Right":
            tobaklib.tMoveRight(controlValue)
        elif param.split(":")[0] == "Turn left":
            tobaklib.tTurnLeft(controlValue)
        elif param.split(":")[0] == "Turn right":
            tobaklib.tTurnRight(controlValue)
        elif param.split(":")[0] == "Forward":
            tobaklib.tMoveForward(controlValue)
        elif param.split(":")[0] == "Backward":
            tobaklib.tMoveBackward(controlValue)
    
    tobaklib.tLanding()
    tobaklib.tHalt()