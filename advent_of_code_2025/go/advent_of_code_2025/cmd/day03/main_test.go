package main

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestParseLineItem(t *testing.T) {
	result := parseLineItem("987654321111111")
	expected := LineItem{LineString: "987654321111111"}
	assert.Equal(t, expected, result)
}

func TestParseLine1(t *testing.T) {
	result := parseLine("987654321111111")
	expected := []LineItem{{LineString: "987654321111111"}}
	assert.Equal(t, expected, result)
}

func TestProcessLine1(t *testing.T) {
	h := NewHelper()
	h.processLine("987654321111111")
	assert.Equal(t, 1, h.numLineitemsProcessed)
	assert.Equal(t, int64(98), h.resultTotal)
}

func TestCalcMaxJoltage(t *testing.T) {
	h := NewHelper()
	// Part One
	// assert.Equal(t, 98, h.calcMaxJoltage("987654321111111"))
	// assert.Equal(t, 89, h.calcMaxJoltage("811111111111119"))
	// assert.Equal(t, 78, h.calcMaxJoltage("234234234234278"))
	// assert.Equal(t, 92, h.calcMaxJoltage("818181911112111"))

	// Part Two
	assert.Equal(t, 987654321111, h.calcMaxJoltage("987654321111111"))
	assert.Equal(t, 811111111119, h.calcMaxJoltage("811111111111119"))
	assert.Equal(t, 434234234278, h.calcMaxJoltage("234234234234278"))
	assert.Equal(t, 888911112111, h.calcMaxJoltage("818181911112111"))
}

// func TestProcessLine2(t *testing.T) {
// 	h := NewHelper()
// 	h.processLine("L11")
// 	assert.Equal(t, 39, h.dial)
// }

func TestProcessAll(t *testing.T) {
	lines := `
987654321111111
811111111111119
234234234234278
818181911112111
`
	lines = strings.TrimSpace(lines)
	fmt.Printf("lines: %v \n", lines)
	reader := strings.NewReader(lines)
	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	// Check result
	assert.Equal(t, 4, h.numLineitemsProcessed)
	// Part 1
	//assert.Equal(t, int64(357), h.resultTotal)
	// Part 2
	assert.Equal(t, int64(3121910778619), h.resultTotal)

}
