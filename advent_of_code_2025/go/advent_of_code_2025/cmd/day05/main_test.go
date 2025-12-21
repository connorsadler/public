package main

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestParseLineConfig1(t *testing.T) {
	result := parseLineConfig("3-5")
	expected := ConfigLine{from: 3, to: 5}
	assert.Equal(t, expected, result)
}

func Test_processItemLine(t *testing.T) {
	tests := []struct {
		name string // description of this test case
		// Named input parameters for target function.
		configLines         []ConfigLine
		line                string
		expectedResultTotal int64
	}{
		{"no config lines", []ConfigLine{}, "1", 0},
		{"single config line - id outside range", []ConfigLine{{10, 20}}, "1", 0},
		{"single config line - id inside range", []ConfigLine{{10, 20}}, "15", 1},
		{"multiple config lines - id outside all ranges", []ConfigLine{{10, 20}, {25, 27}}, "21", 0},
		{"multiple config lines - id inside second range", []ConfigLine{{10, 20}, {25, 27}}, "25", 1},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			h := NewHelper()
			h.configLines = tt.configLines
			h.processItemLine(tt.line)
			assert.Equal(t, tt.expectedResultTotal, h.resultTotal)
		})
	}
}

func TestConfigLineRangeContainsId(t *testing.T) {
	configLine := ConfigLine{from: 3, to: 5}
	assert.Equal(t, false, configLine.rangeContainsId(2))
	assert.Equal(t, true, configLine.rangeContainsId(3))
	assert.Equal(t, true, configLine.rangeContainsId(4))
	assert.Equal(t, true, configLine.rangeContainsId(5))
	assert.Equal(t, false, configLine.rangeContainsId(6))
	assert.Equal(t, false, configLine.rangeContainsId(698))
}

func TestProcessAll(t *testing.T) {
	lines := `
3-5
10-14
16-20
12-18

1
5
8
11
17
32
`
	lines = strings.TrimSpace(lines)
	fmt.Printf("lines: %v \n", lines)
	reader := strings.NewReader(lines)
	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	// Check result
	// Part 1
	assert.Equal(t, int64(3), h.resultTotal)
	// Part 2
	assert.Equal(t, int64(14), h.resultTotalPartTwo)

}

func TestRangeOverlaps(t *testing.T) {
	assert.Equal(t, false, ConfigLine{1, 5}.rangeOverlaps(ConfigLine{6, 7}))
	assert.Equal(t, false, ConfigLine{6, 7}.rangeOverlaps(ConfigLine{1, 5}))
	assert.Equal(t, true, ConfigLine{1, 5}.rangeOverlaps(ConfigLine{5, 7}))
	assert.Equal(t, true, ConfigLine{5, 7}.rangeOverlaps(ConfigLine{1, 5}))
	assert.Equal(t, true, ConfigLine{1, 10}.rangeOverlaps(ConfigLine{4, 5}))
	assert.Equal(t, true, ConfigLine{4, 5}.rangeOverlaps(ConfigLine{1, 10}))
}

func TestMergeRange(t *testing.T) {
	assert.Equal(t, ConfigLine{1, 7}, ConfigLine{1, 5}.mergeRange(ConfigLine{5, 7}))
	assert.Equal(t, ConfigLine{1, 7}, ConfigLine{5, 7}.mergeRange(ConfigLine{1, 5}))

	assert.Equal(t, ConfigLine{1, 10}, ConfigLine{1, 10}.mergeRange(ConfigLine{4, 5}))
	assert.Equal(t, ConfigLine{1, 10}, ConfigLine{4, 5}.mergeRange(ConfigLine{1, 10}))
}

func TestMergeOverlappingRanges(t *testing.T) {
	h := NewHelper()
	h.configLines = []ConfigLine{{1, 10}, {4, 5}, {6, 13}}
	assert.Equal(t, 3, len(h.configLines))
	h.mergeOverlappingRanges()
	assert.Equal(t, 1, len(h.configLines))
	assert.Equal(t, []ConfigLine{{1, 13}}, h.configLines)
}
