package main

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestParseLineItem(t *testing.T) {
	result := parseLineItem("11-22")
	expected := LineItem{From: 11, To: 22}
	assert.Equal(t, expected, result)
}

func TestParseLine1(t *testing.T) {
	result := parseLine("11-22, 401-417")
	expected := []LineItem{{From: 11, To: 22}, {From: 401, To: 417}}
	assert.Equal(t, expected, result)
}

// func TestParseLine2(t *testing.T) {
// 	result := parseLine("L9")
// 	expected := Move{Direction: Left, Amount: 9}
// 	assert.Equal(t, expected, result)
// }

func TestProcessLine1(t *testing.T) {
	h := NewHelper()
	h.processLine("11-22, 401-417")
	assert.Equal(t, 2, h.numLineitemsProcessed)
	assert.Equal(t, int64(33), h.invalidIdTotal)
}

func TestIsIdInvalid(t *testing.T) {
	h := NewHelper()
	assert.Equal(t, false, h.isIdInvalid(123))
	assert.Equal(t, false, h.isIdInvalid(1234))
	assert.Equal(t, true, h.isIdInvalid(1212))
	assert.Equal(t, true, h.isIdInvalid(33))
	assert.Equal(t, true, h.isIdInvalid(330330))
	assert.Equal(t, false, h.isIdInvalid(330331))

	// part two tests
	// Now, an ID is invalid if it is made only of some sequence of digits repeated at least twice.
	// So, 12341234 (1234 two times), 123123123 (123 three times), 1212121212 (12 five times), and 1111111 (1 seven times) are all invalid IDs.
	assert.Equal(t, true, h.isIdInvalid(111))
	assert.Equal(t, true, h.isIdInvalid(1111))
	assert.Equal(t, false, h.isIdInvalid(11113))
	assert.Equal(t, true, h.isIdInvalid(99))
	assert.Equal(t, true, h.isIdInvalid(123123123))
	assert.Equal(t, false, h.isIdInvalid(12312312))
	assert.Equal(t, false, h.isIdInvalid(1))

}

// func TestProcessLine2(t *testing.T) {
// 	h := NewHelper()
// 	h.processLine("L11")
// 	assert.Equal(t, 39, h.dial)
// }

func TestProcessAll(t *testing.T) {
	lines := `
11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
`
	lines = strings.TrimSpace(lines)
	fmt.Printf("lines: %v \n", lines)
	reader := strings.NewReader(lines)
	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	// Check result
	assert.Equal(t, 11, h.numLineitemsProcessed)
	// Part 1 -> assert.Equal(t, int64(1227775554), h.invalidIdTotal)
	// Part 2
	assert.Equal(t, int64(4174379265), h.invalidIdTotal)

}
