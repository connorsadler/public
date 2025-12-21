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
	invalidIdTotal        int64
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

	for id := lineItem.From; id <= lineItem.To; id++ {
		//fmt.Printf("  check id: %v\n", id)
		if h.isIdInvalid(id) {
			h.invalidIdTotal += id
			fmt.Printf("  invalid id: %v\n", id)
			fmt.Printf("  => invalid so adding to total, new total: %v \n", h.invalidIdTotal)
		}
	}
}

func (h *Helper) isIdInvalid(id int64) bool {
	idstr := strconv.FormatInt(id, 10)
	// // Must be even number of digits
	// if len(idstr)%2 != 0 {
	// 	return false
	// }
	// // Check first half equal to second half e.g. 330330 is invalid
	// halflen := len(idstr) / 2
	// firstHalf := idstr[:halflen]
	// secondHalf := idstr[halflen:]
	// return firstHalf == secondHalf

outer:
	for substrlen := 1; substrlen <= len(idstr)/2; substrlen++ {
		// fmt.Printf("Checking %v for substrlen: %v \n", idstr, substrlen)
		if len(idstr)%substrlen != 0 {
			// fmt.Printf("-> Len isnt a multiple of this, so cant be invalid \n")
			continue outer
		}
		substr := idstr[:substrlen]
		// fmt.Printf("  Checking substr: %v \n", substr)
		numSubStrs := len(idstr) / substrlen
		for substrnum := 2; substrnum <= numSubStrs; substrnum++ {
			// fmt.Printf("    Checking substrnum: %v \n", substrnum)
			substrnumsubstr := idstr[(substrnum-1)*substrlen : (substrnum)*substrlen]
			// fmt.Printf("    substrnumsubstr: %v \n", substrnumsubstr)
			if substrnumsubstr != substr {
				// fmt.Printf("    -> mismatch on this substr, so cant be invalid \n")
				continue outer
			}
		}

		// All substrs matched first one, so we're invalid
		// fmt.Printf("  -> we are invalid for substrlen: %v \n", substrlen)
		return true
	}

	// All substrs matched first one, so we're invalid
	// fmt.Printf("-> we are NOT invalid! \n")
	return false
}

func (h *Helper) produceFinalResult() {
	fmt.Printf("--------------------------------------------------\n")
	fmt.Printf("Final result:\n")
	fmt.Printf("  numLineitemsProcessed: %v\n", h.numLineitemsProcessed)
	fmt.Printf("  invalidIdTotal: %v\n", h.invalidIdTotal)
	fmt.Printf("--------------------------------------------------\n")
}

type LineItem struct {
	From int64
	To   int64
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

// lineItem is of form: 11-22
func parseLineItem(lineItem string) LineItem {
	lineItem = strings.TrimSpace(lineItem)
	parts := strings.Split(lineItem, "-")

	from, err := strconv.ParseInt(parts[0], 10, 0)
	if err != nil {
		panic(fmt.Errorf("could not parse lineItem: %v", lineItem))
	}

	to, err := strconv.ParseInt(parts[1], 10, 0)
	if err != nil {
		panic(fmt.Errorf("could not parse lineItem: %v", lineItem))
	}

	return LineItem{From: from, To: to}
}
