package main

import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"log"
	"os"
	"strings"
	"time"
)

func main() {
	fmt.Println("Start")

	filename := "mainfile.txt"
	fileLines := make(chan string, 5)

	// read file in background and output to "fileLines" channel - the channel is closed at the end of the file
	//go readfile1b_withOpenReadAndLoop(filename, fileLines)
	go readfile3_withScanner(filename, fileLines)

	// read from channel
	fmt.Println("reading from channel")
	for {
		inputLine, more := <-fileLines
		if more {
			fmt.Printf("Got this line from channel: %v \n", inputLine)
		} else {
			// Channel was closed, end
			fmt.Printf("Finished reading all lines\n")
			break
		}
	}

	fmt.Println("End")
}

// This reads chunks and sends them to fileLines channel
// It closes the channel at the end of the file
// TODO: This does NOT detect newlines in the file, which is what it really should do
func readfile1b_withOpenReadAndLoop(filename string, fileLines chan string) {
	print := printWithIndent(50)
	print(">>> readfile1b_withOpenReadAndLoop")

	file, err := os.Open(filename)
	if err != nil {
		panic(fmt.Errorf("could not open file: %v, error: %v", filename, err))
	}
	// File implements Reader in file.go
	var reader io.Reader = file

	// read using Read with a byte buffer
	bytebuf := make([]byte, 15)
	loopIteration := 1
	for {
		print("loopIteration: %v \n", loopIteration)
		loopIteration++
		numBytesRead, err := reader.Read(bytebuf)
		print("Read done, result: %v, %v \n", numBytesRead, err)
		// This shows us the full buffer, which is actually wrong - Read may leave parts of the buffer untouched if it doesn't need/write those bytes
		print("  bytes as string: %v \n", string(bytebuf))
		// We should really only look at the first 'numBytesRead' bytes of the buffer
		print("  bytes as string (sub slice): %v \n", string(bytebuf[0:numBytesRead]))

		if err != nil {
			if errors.Is(err, io.EOF) {
				print("EOF found, ending loop\n")
				break
			}
			panic(fmt.Errorf("error reading file: %v, error: %v", filename, err))
		}

		// Send to output channel
		fileLines <- string(bytebuf[0:numBytesRead])

		print("Sleeping to simulate slow read from file \n")
		time.Sleep(time.Millisecond * 500)

		if loopIteration > 10 {
			print("-> Safety net, loop has iterated too many times so stopping it \n")
			break
		}
	}

	close(fileLines)

	print("<<< readfile1b_withOpenReadAndLoop")
}

// This reads chunks and sends them to fileLines channel
// It closes the channel at the end of the file
// This does detect newlines in the file and sends each line in turn
func readfile3_withScanner(filename string, fileLines chan string) {
	print := printWithIndent(50)
	print(">>> readfile3_withScanner")

	file, err := os.Open(filename)
	if err != nil {
		panic(fmt.Errorf("could not open file: %v, error: %v", filename, err))
	}
	// File implements Reader in file.go
	var reader io.Reader = file

	// https://stackoverflow.com/questions/8757389/reading-a-file-line-by-line-in-go

	scanner := bufio.NewScanner(reader)
	// optionally, resize scanner's capacity for lines over 64K, see next example

	print("Start Scanning\n")
	for scanner.Scan() {
		line := scanner.Text()
		print("line: %v \n", line)

		// Send to output channel
		fileLines <- line

		print("Sleeping to simulate slow read from file \n")
		time.Sleep(time.Millisecond * 500)
	}
	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	close(fileLines)

	print("<<< readfile3_withScanner")
}

// Creates and returns a function which can be used instead of fmt.Printf
// The returned function indents each line of the message printed by the specified number of spaces
func printWithIndent(indentByNumSpaces int) func(string, ...any) {
	indentString := strings.Repeat(" ", indentByNumSpaces)
	return func(s string, a ...any) {
		finalMessage := fmt.Sprintf(s, a...)
		lines := strings.Split(finalMessage, "\n")

		// Annoying hack to avoid a blank line being included as the last entry of "lines", if finalMessage ends with a newline
		endsWithNewLine := strings.HasSuffix(finalMessage, "\n")
		if endsWithNewLine && len(lines) > 1 && lines[len(lines)-1] == "" {
			lines = lines[:len(lines)-1]
		}

		// Print all lines with indent prefix
		for _, line := range lines {
			fmt.Println(indentString + line)
		}
	}
}
