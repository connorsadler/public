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
	configMode         bool
	configLines        []ConfigLine
	resultTotal        int64
	resultTotalPartTwo int64
}

func NewHelper() Helper {
	return Helper{configMode: true, configLines: make([]ConfigLine, 0)}
}

func (h *Helper) processLine(line string) {
	if len(line) == 0 {
		// Blank line
		fmt.Printf("Blank line found\n")
		if h.configMode {
			h.configMode = false
			fmt.Printf("Config mode is now off\n")
		} else {
			panic(fmt.Errorf("blank line found when not in config mode - error in file format"))
		}
	} else if h.configMode {
		// Config line
		l := parseLineConfig(line)
		fmt.Printf("parsed config line: %v \n", l)
		fmt.Printf(" range size: %v \n", l.calcRangeSize())
		h.configLines = append(h.configLines, l)
	} else {
		// Item line
		h.processItemLine(line)
	}
}

func (h *Helper) processItemLine(line string) {
	// Parse int value from line
	id, err := strconv.Atoi(line)
	if err != nil {
		panic(fmt.Errorf("error parsing item line: %v", err))
	}

	// Check all configs
	for _, configLine := range h.configLines {
		if configLine.rangeContainsId(id) {
			fmt.Printf("Fresh id %v because it appears in this range: %v \n", id, configLine)
			h.resultTotal++
			break // No need to check any more ranges
		}
	}
}

type ConfigLine struct {
	from int
	to   int
}

func (c ConfigLine) rangeOverlaps(other ConfigLine) bool {
	return c.rangeContainsId(other.from) || c.rangeContainsId(other.to) || other.rangeContainsId(c.from) || other.rangeContainsId(c.to)
}

func (c ConfigLine) mergeRange(other ConfigLine) ConfigLine {
	newFrom := min(c.from, other.from)
	newTo := max(c.to, other.to)
	return ConfigLine{newFrom, newTo}
}

func (c ConfigLine) calcRangeSize() int {
	return c.to - c.from + 1
}

func (c ConfigLine) rangeContainsId(id int) bool {
	return id >= c.from && id <= c.to
}

// line format is: 3-5
func parseLineConfig(line string) ConfigLine {

	elements := strings.Split(line, "-")
	from, err := strconv.Atoi(elements[0])
	if err != nil {
		panic(fmt.Errorf("error parsing config line: %v", err))
	}
	to, err := strconv.Atoi(elements[1])
	if err != nil {
		panic(fmt.Errorf("error parsing config line: %v", err))
	}

	return ConfigLine{from: from, to: to}
}

func (h *Helper) produceFinalResult() {
	fmt.Printf("--------------------------------------------------\n")
	fmt.Printf("Final result:\n")
	fmt.Printf("  resultTotal: %v\n", h.resultTotal)
	fmt.Printf("--------------------------------------------------\n")

	// Part Two
	h.mergeOverlappingRanges()
	h.resultTotalPartTwo = 0
	for i := 0; i < len(h.configLines); i++ {
		h.resultTotalPartTwo += int64(h.configLines[i].calcRangeSize())
	}
	fmt.Printf("final result, part two: %v \n", h.resultTotalPartTwo)

}

func (h *Helper) mergeOverlappingRanges() {
	fmt.Println(">>> mergeOverlappingRanges")
	passNum := 1
	for {
		fmt.Printf("Starting pass: %v \n", passNum)
		fmt.Printf("  number of config lines: %v \n", len(h.configLines))
		passNum++

		// Keep trying to merge each range with another overlapping range, if any
		// Only stop when there are no overlapping ranges
		foundOverlap := false
		for i := 0; i < len(h.configLines); i++ {
			for j := 0; j < len(h.configLines); j++ {
				if i != j {
					if h.configLines[i].rangeOverlaps(h.configLines[j]) {
						fmt.Printf("Range overlaps: %v and %v - merging \n", h.configLines[i], h.configLines[j])
						// Merge
						h.configLines[i] = h.configLines[i].mergeRange(h.configLines[j])
						fmt.Printf("  merge result: %v \n", h.configLines[i])
						// Delete other entry - from https://go.dev/wiki/SliceTricks
						h.configLines = append(h.configLines[:j], h.configLines[j+1:]...)

						// Signal found overlap so we will try the main loop again
						foundOverlap = true
					}
				}
			}
		}
		if !foundOverlap {
			break
		}
	}
	fmt.Println("<<< mergeOverlappingRanges")
}
