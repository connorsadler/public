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
		fmt.Printf("line: >>>%v<<< \n", line)

		h.processLine(line)
	}
	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

}

type Helper struct {
	configMode          bool
	configLines         []ConfigLine
	configLines2        []ConfigLine2
	operatorsLine       OperatorsLine
	parsedColumnNumbers [][]int
	resultTotal         int
	resultTotalPartTwo  int
}

func NewHelper() Helper {
	return Helper{configMode: true, configLines: make([]ConfigLine, 0)}
}

func (h *Helper) processLine(line string) {
	// strings.Fields copes with multiples spaces (sometimes one space, sometimes more) between elements
	elements := strings.Fields(line)
	fmt.Printf("elements len: %v \n", len(elements))

	// Check for an operator in first element
	firstElement := elements[0]
	if _, ok := operatorStringToOperator[firstElement]; ok {
		// Operator line (last line of file)
		operatorsLine := parseLineOperators(elements)
		h.operatorsLine = operatorsLine
	} else {
		// Numbers line
		configLine := parseLineConfig(elements)
		h.configLines = append(h.configLines, configLine)
		// Part two - need unparsed line
		configLine2 := ConfigLine2{line: line}
		h.configLines2 = append(h.configLines2, configLine2)
	}

	// detect the last line by checking for an operator
	// if not the last line, attempt to parse as ConfigLine (line with numbers)
}

type ConfigLine struct {
	numbers []int
}

type ConfigLine2 struct {
	line string
}

type Operator int

const (
	OperatorPlus Operator = iota
	OperatorMultiply
)

type OperatorsLine struct {
	operators []Operator
}

var operatorStringToOperator map[string]Operator = make(map[string]Operator)

func init() {
	operatorStringToOperator["*"] = OperatorMultiply
	operatorStringToOperator["+"] = OperatorPlus
}

// line format is: "123 328  51 64 "
func parseLineConfig(elements []string) ConfigLine {
	numbers := make([]int, len(elements))
	for i := 0; i < len(elements); i++ {
		var err error
		element := elements[i]
		numbers[i], err = strconv.Atoi(element)
		if err != nil {
			panic(fmt.Errorf("error parsing line element: %v, error: %v", element, err))
		}
	}
	return ConfigLine{numbers: numbers}
}

// line format is: "*   +   *   + "
func parseLineOperators(elements []string) OperatorsLine {
	operators := make([]Operator, len(elements))
	for i := 0; i < len(elements); i++ {
		operators[i] = operatorStringToOperator[elements[i]]
	}
	return OperatorsLine{operators: operators}
}

func (h *Helper) produceFinalResult() {

	h.processOperatorsCalcForPartTwo()
	h.processOperatorsCalc()

	fmt.Printf("--------------------------------------------------\n")
	fmt.Printf("Final result:\n")
	fmt.Printf("  resultTotal: %v\n", h.resultTotal)
	fmt.Printf("  resultTotalPartTwo: %v\n", h.resultTotalPartTwo)
	fmt.Printf("--------------------------------------------------\n")

	// Part Two
	// TODO

}

func (h *Helper) processOperatorsCalc() {

	for colIdx := 0; colIdx < len(h.operatorsLine.operators); colIdx++ {
		var resultForOperator int
		var resultForOperatorPartTwo int
		operator := h.operatorsLine.operators[colIdx]
		switch operator {
		case OperatorMultiply:
			resultForOperator = h.applyOperator(colIdx, func(a, b int) int {
				return a * b
			})
			resultForOperatorPartTwo = h.applyOperatorPartTwo(colIdx, func(a, b int) int {
				return a * b
			})
		case OperatorPlus:
			resultForOperator = h.applyOperator(colIdx, func(a, b int) int {
				return a + b
			})
			resultForOperatorPartTwo = h.applyOperatorPartTwo(colIdx, func(a, b int) int {
				return a + b
			})
		default:
			panic(fmt.Errorf("unsupported operator: %v", operator))
		}

		// Add to total
		h.resultTotal += resultForOperator
		h.resultTotalPartTwo += resultForOperatorPartTwo
	}
}

// Populates h.parsedColumnNumbers
func (h *Helper) processOperatorsCalcForPartTwo() {
	// Parse ConfigLine2 items
	fmt.Printf("Parse ConfigLine2 items\n")

	// 	var mask string
	// 	for i, configLine2 := range h.configLines2 {
	// 		fmt.Printf("line %v has len: %v\n", i, len(configLine2.line))
	// 		if i == 0 {
	// 			mask = configLine2.line
	// 		} else {
	// 			mask = overlayStrings(mask, configLine2.line, false)
	// 		}
	// 	}
	// 	fmt.Printf("final mask: %v \n", mask)
	// 	maskElements := strings.Fields(mask)
	// 	numCols := len(maskElements)
	// 	fmt.Printf("num columns: %v \n", numCols)
	//
	// 	// Calc width of each column
	// 	colStartIdx := 0
	// 	prevColWidth := 0
	// 	for colIdx := 0; colIdx < numCols; colIdx++ {
	// 		colStartIdx += prevColWidth
	// 		if colIdx > 0 {
	// 			colStartIdx += 1
	// 		}
	//
	// 		currentColWidth := len(maskElements[colIdx])
	// 		fmt.Printf("col: %v has width: %v \n", colIdx, currentColWidth)
	// 		fmt.Printf("  colStartIdx: %v \n", colStartIdx)
	//
	// 		// Iterate all lines for this column
	// 		for i, configLine2 := range h.configLines2 {
	// 			columnContents := configLine2.line[colStartIdx : colStartIdx+currentColWidth]
	// 			fmt.Printf("    line %v has column contents: %v\n", i, columnContents)
	// 		}
	//
	// 		prevColWidth = currentColWidth
	// 	}

	// Got partway through the above and then spotted we could work along the string and for each position in the string we add to a string for that column
	// e.g.
	//     123 328  51 64
	//      45 64  387 23
	//       6 98  215 314
	//     ^ string here is "1  "
	//      ^ string here is "24 "
	//       ^ string here is "356"
	//        ^ string here is "   "
	//         etc
	// We can then:
	// - detect column break by detecting an empty string
	// - parse non empty strings as numbers

	stringsReadingDownColumn := make([]string, 0)
	for i, configLine2 := range h.configLines2 {

		fmt.Printf("line %v has len: %v\n", i, len(configLine2.line))
		for strIdx := 0; strIdx < len(configLine2.line); strIdx++ {
			if len(stringsReadingDownColumn) < strIdx+1 {
				stringsReadingDownColumn = append(stringsReadingDownColumn, "")
			}
			ch := configLine2.line[strIdx]
			stringsReadingDownColumn[strIdx] = stringsReadingDownColumn[strIdx] + string(ch)
		}
	}
	fmt.Printf("final stringsReadingDownColumn: %v \n", stringsReadingDownColumn)

	// Parse column numbers - see notes/example above
	parsedColumnNumbers := make([][]int, 0)
	columnIdx := 0
	for _, strReadingDown := range stringsReadingDownColumn {
		strReadingDownTrimmed := strings.TrimSpace(strReadingDown)
		if strReadingDownTrimmed == "" {
			columnIdx++
			continue
		}
		fmt.Printf("columnIdx: %v, strReadingDownTrimmed: %v \n", columnIdx, strReadingDownTrimmed)
		numberReadingDown, err := strconv.Atoi(strReadingDownTrimmed)
		if err != nil {
			panic(fmt.Errorf("error parsing string reading down column: %v, error: %v", strReadingDownTrimmed, err))
		}

		if len(parsedColumnNumbers) < columnIdx+1 {
			parsedColumnNumbers = append(parsedColumnNumbers, nil)
		}
		p := parsedColumnNumbers[columnIdx]
		p = append(p, numberReadingDown)
		parsedColumnNumbers[columnIdx] = p
	}

	h.parsedColumnNumbers = parsedColumnNumbers

	// Do operators calc
	// ... done in normal (Part one) method
}

func overlayStrings(s1, s2 string, overwriteWithSpaces bool) string {
	resultBytes := ([]byte)(s1)
	// resultBytes[0] = byte('_')
	// fmt.Printf("s1: %v \n", s1)
	// fmt.Printf("s2: %v \n", s2)

	for i := 0; i < len(s2); i++ {
		if overwriteWithSpaces {
			resultBytes[i] = s2[i]
		} else {
			// Dont overwrite a non-space with a space
			if s2[i] != ' ' {
				resultBytes[i] = s2[i]
			}
		}
	}

	return string(resultBytes)
}

func (h *Helper) applyOperator(colIdx int, operatorFunc func(a int, b int) int) int {
	var result int
	for rowIdx := 0; rowIdx < len(h.configLines); rowIdx++ {
		cellValue := h.configLines[rowIdx].numbers[colIdx]
		if rowIdx == 0 {
			result = cellValue
		} else {
			result = operatorFunc(result, cellValue)
		}
	}
	return result
}

func (h *Helper) applyOperatorPartTwo(colIdx int, operatorFunc func(a int, b int) int) int {
	var result int

	columnNumbers := h.parsedColumnNumbers[colIdx]
	for idx, columnNumber := range columnNumbers {
		if idx == 0 {
			result = columnNumber
		} else {
			result = operatorFunc(result, columnNumber)
		}

	}

	// for rowIdx := 0; rowIdx < len(h.configLines); rowIdx++ {
	// 	cellValue := h.configLines[rowIdx].numbers[colIdx]
	// 	if rowIdx == 0 {
	// 		result = cellValue
	// 	} else {
	// 		result = operatorFunc(result, cellValue)
	// 	}
	// }
	return result
}
