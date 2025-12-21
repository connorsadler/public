package main

import (
	"fmt"
	"strings"
	"testing"
)

func TestSplit(t *testing.T) {
	msg := "hello world \n"
	//msg := "loopIteration: 1 \n"
	lines := strings.Split(msg, "\n")
	for _, line := range lines {
		fmt.Printf("line: %v \n", line)
	}
}
