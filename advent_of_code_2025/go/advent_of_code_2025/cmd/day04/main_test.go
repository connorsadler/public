package main

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestParseLine1(t *testing.T) {
	result := parseLine("..@@.@@@@.")
	expected := Line{lineContents: []int{0, 0, 1, 1, 0, 1, 1, 1, 1, 0}}
	assert.Equal(t, expected, result)
}

func TestProcessAll(t *testing.T) {
	lines := `
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
`
	lines = strings.TrimSpace(lines)
	fmt.Printf("lines: %v \n", lines)
	reader := strings.NewReader(lines)
	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	// Check result
	// Part 1
	// assert.Equal(t, int64(13), h.resultTotal)
	// Part 2
	assert.Equal(t, int64(43), h.resultTotal)

}
