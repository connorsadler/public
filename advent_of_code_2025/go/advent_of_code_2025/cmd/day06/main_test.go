package main

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestProcessLine1_ConfigLine(t *testing.T) {
	h := NewHelper()
	h.processLine("123  328   51 64")
	assert.Equal(t, 1, len(h.configLines))
}

func TestProcessLine2_OperatorsLine(t *testing.T) {
	h := NewHelper()
	h.processLine("*   +   *   + ")
	assert.Equal(t, OperatorsLine{[]Operator{OperatorMultiply, OperatorPlus, OperatorMultiply, OperatorPlus}}, h.operatorsLine)
}
func TestParseLineConfig1(t *testing.T) {
	result := parseLineConfig([]string{"123", "328", "51", "64"})
	expected := ConfigLine{[]int{123, 328, 51, 64}}
	assert.Equal(t, expected, result)
}

func TestParseLineOperators(t *testing.T) {
	result := parseLineOperators([]string{"*", "+", "*", "+"})
	expected := OperatorsLine{[]Operator{OperatorMultiply, OperatorPlus, OperatorMultiply, OperatorPlus}}
	assert.Equal(t, expected, result)
}

func TestProcessAll(t *testing.T) {
	lines := `
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   + 
`
	lines = strings.TrimSpace(lines)
	fmt.Printf("lines: %v \n", lines)
	reader := strings.NewReader(lines)
	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	// Check result
	// Part 1
	assert.Equal(t, 4277556, h.resultTotal)
	// Part 2
	assert.Equal(t, 3263827, h.resultTotalPartTwo)

}

func TestHelper_processOperatorsCalc(t *testing.T) {
	h := NewHelper()

	h.processOperatorsCalc()

	assert.Equal(t, int64(0), h.resultTotal)
}

func Test_overlayStrings(t *testing.T) {
	assert.Equal(t, "AAAX YYY ZZZ ", overlayStrings(" XXX YYY ZZZ ", "AAA", true))
	assert.Equal(t, "   B YYY ZZZ ", overlayStrings(" XXX YYY ZZZ ", "   B", true))

	assert.Equal(t, " XXB YYY ZZZ ", overlayStrings(" XXX YYY ZZZ ", "   B", false))

}

func TestHelper_processOperatorsCalcForPartTwo(t *testing.T) {
	h := NewHelper()
	h.configLines2 = append(h.configLines2, ConfigLine2{line: "123 328  51 64 "})
	h.configLines2 = append(h.configLines2, ConfigLine2{line: " 45 64  387 23 "})
	h.configLines2 = append(h.configLines2, ConfigLine2{line: "  6 98  215 314"})

	h.processOperatorsCalcForPartTwo()
	assert.Equal(t, []int{1, 24, 356}, h.parsedColumnNumbers[0])
	assert.Equal(t, []int{369, 248, 8}, h.parsedColumnNumbers[1])
	assert.Equal(t, []int{32, 581, 175}, h.parsedColumnNumbers[2])
	assert.Equal(t, []int{623, 431, 4}, h.parsedColumnNumbers[3])

}
