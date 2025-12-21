package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"os"
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
	lines       []Line
	resultTotal int64
}

func NewHelper() Helper {
	return Helper{lines: make([]Line, 0)}
}

func (h *Helper) processLine(line string) {
	l := parseLine(line)
	fmt.Printf("parsed line: %v \n", l)
	h.lines = append(h.lines, l)
}

type Line struct {
	// 0 - empty, 1 - roll of paper
	lineContents []int
}

func parseLine(line string) Line {
	lineContents := make([]int, len(line))
	for i, ch := range line {
		isRollOfPaper := ch == rune('@')
		var isRollOfPaperInt int
		if isRollOfPaper {
			isRollOfPaperInt = 1
		} else {
			isRollOfPaperInt = 0
		}
		lineContents[i] = isRollOfPaperInt
	}

	return Line{lineContents: lineContents}
}

func (h *Helper) produceFinalResult() {

	pass := 1
	for {
		fmt.Printf("PASS: %v\n", pass)
		changeMadeThisPass := h.countRollsThatCanBeAccessed()
		fmt.Printf("PASS: %v, changes made: %v\n", pass, changeMadeThisPass)
		if changeMadeThisPass == 0 {
			break
		}
		pass++
	}

	fmt.Printf("--------------------------------------------------\n")
	fmt.Printf("Final result:\n")
	fmt.Printf("  resultTotal: %v\n", h.resultTotal)
	fmt.Printf("--------------------------------------------------\n")
}

func (h *Helper) countRollsThatCanBeAccessed() int {
	resultChangesMade := 0

	for r := 0; r < len(h.lines); r++ {
		line := h.lines[r]
		for c := 0; c < len(line.lineContents); c++ {
			if h.isRollAt(r, c) {
				fmt.Printf("checking roll at: %v, %v\n", r, c)

				countSurroundingCellsWithRolls := 0
			iterateSurroundingCells:
				for dr := -1; dr <= 1; dr++ {
					for dc := -1; dc <= 1; dc++ {
						// Dont check ourself ;]
						if dr == 0 && dc == 0 {
							continue
						}

						// fmt.Printf("  checking surrounding cell at: %v, %v\n", r+dr, c+dc)
						if h.isRollAt(r+dr, c+dc) {
							countSurroundingCellsWithRolls++
							// fmt.Printf("  -> found, surrounding count now: %v \n", countSurroundingCellsWithRolls)
							// We found too many so can stop now
							if countSurroundingCellsWithRolls == 4 {
								break iterateSurroundingCells
							}
						}
					}
				}

				if countSurroundingCellsWithRolls < 4 {
					fmt.Printf("==> result is YES\n")
					h.resultTotal++
					// remove this roll
					h.lines[r].lineContents[c] = 0
					// mark result
					resultChangesMade++
				} else {
					fmt.Printf("==> result is NO\n")
				}
			}
		}
	}

	return resultChangesMade
}

func (h *Helper) isRollAt(r int, c int) bool {
	// Row invalid? There cannot be a roll there
	if r < 0 || r >= len(h.lines) {
		return false
	}
	// Col invalid? There cannot be a roll there
	lineContents := h.lines[r].lineContents
	if c < 0 || c >= len(lineContents) {
		return false
	}
	return lineContents[c] > 0
}
