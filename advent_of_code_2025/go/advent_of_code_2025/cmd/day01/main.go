package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"os"
	"strconv"
)

func main() {
	fmt.Println("Start")

	//input, err := cfsutils.ReadFile("input.txt")
	reader, err := os.Open("input.txt")
	if err != nil {
		panic(fmt.Errorf("error reading file: %v", err))
	}

	// Read line by line
	// bufReader := bufio.NewReader(reader)
	// for {
	// 	strFromFile, err := bufReader.ReadString('\n')
	// 	if err != nil {
	// 		if errors.Is(err, io.EOF) { // prefered way by GoLang doc
	// 			result = append(result, removeTrailing(strFromFile, '\n'))
	// 			break
	// 		}
	// 		return nil, fmt.Errorf("ReadFileToLines0 - Error during read: %v", err)
	// 	}
	// 	result = append(result, removeTrailing(strFromFile, '\n'))
	// }

	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	fmt.Println("End")
}

func processAllLines(reader io.Reader, h *Helper) {
	// https://stackoverflow.com/questions/8757389/reading-a-file-line-by-line-in-go
	scanner := bufio.NewScanner(reader)
	// optionally, resize scanner's capacity for lines over 64K, see next example

	for scanner.Scan() {
		line := scanner.Text()
		fmt.Printf("line: %v \n", line)

		h.processLine(line)
	}
	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

}

type Helper struct {
	dial                           int
	numTimesAtZero                 int
	numTimesPastZeroDuringRotation int
}

func NewHelper() Helper {
	return Helper{dial: 50, numTimesAtZero: 0, numTimesPastZeroDuringRotation: 0}
}

func (h *Helper) processLine(line string) {
	fmt.Printf("  dial: %v\n", h.dial)

	// line is of format: L68 or R48
	move := parseLine(line)
	fmt.Printf("  move: %+v \n", move)

	// Move dial

	if move.Amount >= 100 {
		fullTurns := move.Amount / 100
		fmt.Printf("  fullTurns: %v\n", fullTurns)
		move.Amount = move.Amount % 100
		fmt.Printf("  new move.Amount: %v\n", move.Amount)

		var incNumTimesPastZeroDuringRotation int
		if h.dial == 0 && move.Amount == 0 {
			// This version gives 6374 which is incorrect - BUT I fixed it by sending less scenarios down this branch by checking for "move.amount==0" also
			// We now get 6599 which is correct, as more scenarios use the 'else' branch
			incNumTimesPastZeroDuringRotation += fullTurns - 1
		} else {
			incNumTimesPastZeroDuringRotation += fullTurns
		}
		fmt.Printf("  incNumTimesPastZeroDuringRotation: %v \n", incNumTimesPastZeroDuringRotation)
		h.numTimesPastZeroDuringRotation += incNumTimesPastZeroDuringRotation
		fmt.Printf("  new h.numTimesPastZeroDuringRotation: %v \n", h.numTimesPastZeroDuringRotation)
	}

	oldDial := h.dial
	if move.Direction == Left {
		h.dial -= move.Amount
		if h.dial < 0 {
			h.dial += 100
			if h.dial != 0 && oldDial != 0 {
				h.numTimesPastZeroDuringRotation += 1
				fmt.Printf("  add to NumTimesPastZeroDuringRotation\n")
				fmt.Printf("  new h.numTimesPastZeroDuringRotation: %v \n", h.numTimesPastZeroDuringRotation)
			} else {
				fmt.Printf("  Check\n")
			}
		}
	} else {
		h.dial += move.Amount
		if h.dial >= 100 {
			h.dial -= 100
			if h.dial != 0 && oldDial != 0 {
				h.numTimesPastZeroDuringRotation += 1
				fmt.Printf("  add to NumTimesPastZeroDuringRotation\n")
				fmt.Printf("  new h.numTimesPastZeroDuringRotation: %v \n", h.numTimesPastZeroDuringRotation)
			} else {
				fmt.Printf("  Check\n")
			}
		}
	}

	fmt.Printf("  new dial: %v\n", h.dial)
	if h.dial == 0 {
		fmt.Printf("  FOUND ZERO DIAL\n")
		h.numTimesAtZero += 1
		fmt.Printf("  new h.numTimesAtZero: %v \n", h.numTimesAtZero)
	}
}

func (h *Helper) produceFinalResult() {
	fmt.Printf("--------------------------------------------------\n")
	fmt.Printf("Final result:\n")
	fmt.Printf("  numTimesAtZero: %v\n", h.numTimesAtZero)
	fmt.Printf("  numTimesPastZeroDuringRotation: %v\n", h.numTimesPastZeroDuringRotation)
	fmt.Printf("\n")
	fmt.Printf("  Total result: %v\n", h.numTimesAtZero+h.numTimesPastZeroDuringRotation)
	fmt.Printf("--------------------------------------------------\n")
}

type DirectionType int

const (
	Left DirectionType = iota
	Right
)

type Move struct {
	Direction DirectionType
	Amount    int
}

var _ fmt.Stringer = Move{}

func (m Move) String() string {
	var directionAsString string
	if m.Direction == Left {
		directionAsString = "Left"
	} else {
		directionAsString = "Right"
	}
	return fmt.Sprintf("Move(%v , %v)", directionAsString, m.Amount)
}

func parseLine(line string) Move {
	firstChar := line[0]
	rest := line[1:]

	var direction DirectionType
	if firstChar == 'L' {
		direction = Left
	} else {
		direction = Right
	}

	restInt, err := strconv.Atoi(rest)
	if err != nil {
		panic(fmt.Errorf("could not parse line: %v", line))
	}

	return Move{Direction: direction, Amount: restInt}
}
