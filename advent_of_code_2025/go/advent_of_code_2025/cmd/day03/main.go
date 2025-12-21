package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"os"
	"strconv"
	"strings"
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
	numLineitemsProcessed int
	resultTotal           int64
}

func NewHelper() Helper {
	return Helper{}
}

func (h *Helper) processLine(line string) {
	// fmt.Printf("  dial: %v\n", h.dial)

	// line contains a number of lineItems
	lineItems := parseLine(line)
	fmt.Printf("  lineItems: %v \n", lineItems)

	// Parse each lineItem
	for _, lineItem := range lineItems {
		h.processLineItem(lineItem)
	}
}

func (h *Helper) processLineItem(lineItem LineItem) {
	fmt.Printf("processLineItem: %+v\n", lineItem)
	h.numLineitemsProcessed += 1

	joltage := h.calcMaxJoltage(lineItem.LineString)
	h.resultTotal += int64(joltage)
}

func (h *Helper) calcMaxJoltage(lineItemString string) int {
	fmt.Printf("calcMaxJoltage for: %v \n", lineItemString)

	usev1 := true
	if usev1 {
		return h.calcMaxJoltagev1(lineItemString)
	} else {
		return h.calcMaxJoltagev2(lineItemString)
	}
}

// v1
// Max use of separate function 'findMaxDigit'
func (h *Helper) calcMaxJoltagev1(lineItemString string) int {

	// 	// Two digits
	// 	// digit1, digit1Idx := findMaxDigit(lineItemString, 0, len(lineItemString)-2)
	// 	// digit2, _ := findMaxDigit(lineItemString, digit1Idx+1, len(lineItemString)-1)
	// 	// fmt.Printf("digits found: %v %v \n", digit1, digit2)
	// 	//
	// 	// return 10*digit1 + digit2

	// 12 digits
	prevDigitIdx := -1
	result := 0
	for digitNum := 1; digitNum <= 12; digitNum++ {
		digitsLeftAfterThisOne := 12 - digitNum
		digit, digitIdx := findMaxDigit(lineItemString, prevDigitIdx+1, len(lineItemString)-1-digitsLeftAfterThisOne)
		prevDigitIdx = digitIdx
		result = result*10 + digit
	}

	return result
}

func findMaxDigit(lineItemString string, startIdx, endIdx int) (int, int) {
	result := 0
	resultIdx := 0
	for i := startIdx; i <= endIdx; i++ {
		digit, err := strconv.Atoi(string(lineItemString[i]))
		if err != nil {
			panic(fmt.Errorf("could not parse lineItemString: %v at idx: %v", lineItemString, i))
		}
		if digit > result {
			result = digit
			resultIdx = i
		}
	}
	return result, resultIdx
}

// v2
// Single function version, for comparision
func (h *Helper) calcMaxJoltagev2(lineItemString string) int {

	currentMax := 0
	currentMaxAtIdx := -1
	currentDigitNum := 1
	digitsAfterThisOne := 11
	result := 0
	for i := 0; i < len(lineItemString); i++ {
		currentDigit, err := strconv.Atoi(string(lineItemString[i]))
		if err != nil {
			panic(fmt.Errorf("could not parse lineItemString: %v at idx: %v", lineItemString, i))
		}

		// fmt.Printf("  searching for digit %v, search idx %v -> %v \n", currentDigitNum, i, currentDigit)

		if currentDigit > currentMax {
			currentMax = currentDigit
			currentMaxAtIdx = i
		}
		// If we haven't enough digits left, our current max will have to do
		if i == len(lineItemString)-1-digitsAfterThisOne {
			// Current digit search has ended or we'd run out of digits for the digits after this one - our current max will have to do
			result = result*10 + currentMax
			if digitsAfterThisOne == 0 {
				break
			}
			currentDigitNum++
			digitsAfterThisOne -= 1
			currentMax = 0
			// We need to start searching for the next digit at the point just after the current digit was found, so we 'move back'
			i = currentMaxAtIdx
		}
	}

	return result
}

func (h *Helper) produceFinalResult() {
	fmt.Printf("--------------------------------------------------\n")
	fmt.Printf("Final result:\n")
	fmt.Printf("  numLineitemsProcessed: %v\n", h.numLineitemsProcessed)
	fmt.Printf("  resultTotal: %v\n", h.resultTotal)
	fmt.Printf("--------------------------------------------------\n")
}

type LineItem struct {
	LineString string
}

// var _ fmt.Stringer = LineItem{}
//
// func (li LineItem) String() string {
// 	// return fmt.Sprintf("Move(%v , %v)", directionAsString, m.Amount)
// 	return "TODO"
// }

func parseLine(line string) []LineItem {
	lineItems := strings.Split(line, ",")
	result := make([]LineItem, 0)
	for _, lineItem := range lineItems {
		li := parseLineItem(lineItem)
		result = append(result, li)
	}
	return result
}

// lineItem is of form: XXX
func parseLineItem(lineItem string) LineItem {
	lineItemStr := strings.TrimSpace(lineItem)
	return LineItem{LineString: lineItemStr}
}
