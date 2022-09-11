fun equalityTest(l1: List<Int>, l2: List<Int>){
    if(bitVGen(l1).equals(bitVGen(l2))){
        println("A két lista megegyezik")
    }else{
        println("A két lista nem egyezik meg")
    }
}

fun bitVGen(tempList : List<Int>): String{
    var checkedElements = mutableListOf<Int>()
    var elementCounter = mutableListOf<Int>()

    for(i in 0 until tempList.size){
        if(checkedElements.contains(tempList[i])){
            elementCounter[checkedElements.indexOf(tempList[i])]++
        }else{
            checkedElements.add(tempList[i])
            elementCounter.add(1)
        }
    }

    for(i in 0 until checkedElements.size-1){
        for(j in i until checkedElements.size){
            if(checkedElements[i]>checkedElements[j]){
                var tempE = checkedElements[i]
                checkedElements[i]=checkedElements[j]
                checkedElements[j]=tempE

                var tempC = elementCounter[i]
                elementCounter[i]=elementCounter[j]
                elementCounter[j]=tempC
            }
        }
    }

    println(checkedElements.toString()+elementCounter.toString())

    return checkedElements.toString()+elementCounter.toString()
}

fun main(args : Array<String>){
    val control = listOf(2, 7, 3, 2, 2, 7, 5, 4, 3, -4, -2)
    val t1List = listOf(6, 2, 4, 6)
    val t2List = listOf(6, 37, 233, -76, 5, 9, 2, 6, -4, -4, -2)
    val controlWithDiffOrder = control.sorted()

    print("t1List, control: ")
    equalityTest(t1List,control)
    print("t2List, control: ")
    equalityTest(t2List,control)
    print("control, control: ")
    equalityTest(control,control)
    print("controlWithDiffOrder, control: ")
    equalityTest(controlWithDiffOrder,control)
}
