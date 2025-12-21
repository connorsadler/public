package main

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestParseLine1(t *testing.T) {
	result := parseLine("R12")
	expected := Move{Direction: Right, Amount: 12}
	assert.Equal(t, expected, result)
}

func TestParseLine2(t *testing.T) {
	result := parseLine("L9")
	expected := Move{Direction: Left, Amount: 9}
	assert.Equal(t, expected, result)
}

func TestProcessLine1(t *testing.T) {
	h := NewHelper()
	h.processLine("R10")
	assert.Equal(t, 60, h.dial)
}

func TestProcessLine2(t *testing.T) {
	h := NewHelper()
	h.processLine("L11")
	assert.Equal(t, 39, h.dial)
}

func TestProcessLine3(t *testing.T) {
	h := NewHelper()
	h.dial = 5
	h.processLine("L10")
	assert.Equal(t, 95, h.dial)
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine4(t *testing.T) {
	h := NewHelper()
	h.dial = 98
	h.processLine("R10")
	assert.Equal(t, 8, h.dial)
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine5(t *testing.T) {
	h := NewHelper()
	h.dial = 99
	h.processLine("R150")
	assert.Equal(t, 49, h.dial)
}

func TestProcessLine6(t *testing.T) {
	h := NewHelper()
	h.dial = 2
	h.processLine("L150")
	assert.Equal(t, 52, h.dial)
}

func TestProcessLine7a_startZero_left1(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("L1")
	assert.Equal(t, 99, h.dial)
	// We moved AWAY from zero, we did not move past it or onto it
	assert.Equal(t, 0, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine7b_startZero_right100(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("R100")
	assert.Equal(t, 0, h.dial)
	// We moved past zero once but ended on zero so it doesn't count
	assert.Equal(t, 0, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine7b_start1_right100(t *testing.T) {
	h := NewHelper()
	h.dial = 1
	h.processLine("R100")
	assert.Equal(t, 1, h.dial)
	// We moved past zero once and ended on non zero so counts
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine7c_startZero_right200(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("R200")
	assert.Equal(t, 0, h.dial)
	// We moved past zero once and ended on zero
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine7d_start1_right200(t *testing.T) {
	h := NewHelper()
	h.dial = 1
	h.processLine("R200")
	assert.Equal(t, 1, h.dial)
	// We moved past zero once and ended on zero
	assert.Equal(t, 2, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine8b_startZero_left100(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("L100")
	assert.Equal(t, 0, h.dial)
	// We moved past zero once but ended on zero so it doesn't count
	assert.Equal(t, 0, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine8b_start1_left100(t *testing.T) {
	h := NewHelper()
	h.dial = 1
	h.processLine("L100")
	assert.Equal(t, 1, h.dial)
	// We moved past zero once and ended on non zero so counts
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine8c_startZero_left200(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("L200")
	assert.Equal(t, 0, h.dial)
	// We moved past zero once and ended on zero
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine8d_start1_left200(t *testing.T) {
	h := NewHelper()
	h.dial = 1
	h.processLine("L200")
	assert.Equal(t, 1, h.dial)
	// We moved past zero once and ended on zero
	assert.Equal(t, 2, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine9a_start1_right99(t *testing.T) {
	h := NewHelper()
	h.dial = 1
	h.processLine("R99")
	assert.Equal(t, 0, h.dial)
	assert.Equal(t, 0, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine9b_start1_right199(t *testing.T) {
	h := NewHelper()
	h.dial = 1
	h.processLine("R199")
	assert.Equal(t, 0, h.dial)
	assert.Equal(t, 1, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine10a_start0_left238(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("L238")
	assert.Equal(t, 62, h.dial)
	assert.Equal(t, 2, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine10b_start0_left300(t *testing.T) {
	h := NewHelper()
	h.dial = 0
	h.processLine("L300")
	assert.Equal(t, 0, h.dial)
	assert.Equal(t, 2, h.numTimesPastZeroDuringRotation)
}

// Be careful: if the dial were pointing at 50, a single rotation like R1000
// would cause the dial to point at 0 ten times before returning back to 50!
func TestProcessLine20a_example_big_move(t *testing.T) {
	h := NewHelper()
	h.dial = 50
	h.processLine("R1000")
	assert.Equal(t, 50, h.dial)
	assert.Equal(t, 10, h.numTimesPastZeroDuringRotation)
}

func TestProcessLine20b_example_big_move(t *testing.T) {
	h := NewHelper()
	h.dial = 50
	h.processLine("L1000")
	assert.Equal(t, 50, h.dial)
	assert.Equal(t, 10, h.numTimesPastZeroDuringRotation)
}

func TestProcessAll(t *testing.T) {
	lines := `
L68
L30
R48
L5
R60
L55
L1
L99
R14
L82
`
	lines = strings.TrimSpace(lines)
	fmt.Printf("lines: %v \n", lines)
	reader := strings.NewReader(lines)
	h := NewHelper()
	processAllLines(reader, &h)
	h.produceFinalResult()

	// Check result
	assert.Equal(t, 3, h.numTimesAtZero)
	assert.Equal(t, 3, h.numTimesPastZeroDuringRotation)
}
