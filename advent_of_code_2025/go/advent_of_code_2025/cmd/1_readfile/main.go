package main

import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"log"
	"os"
)

func main() {
	fmt.Println("Start")

	filename := "mainfile.txt"
	readfile1_withOpenRead(filename)
	readfile1b_withOpenReadAndLoop(filename)
	readfile2_os_ReadFile(filename)
	readfile3_withScanner(filename)

	fmt.Println("End")
}

func readfile1_withOpenRead(filename string) {
	fmt.Println("readfile1_withOpenRead")

	file, err := os.Open(filename)
	if err != nil {
		panic(fmt.Errorf("could not open file: %v, error: %v", filename, err))
	}
	// File implements Reader in file.go
	var reader io.Reader = file

	// read using Read with a byte buffer
	bytebuf := make([]byte, 300)
	numBytesRead, err := reader.Read(bytebuf)
	fmt.Printf("Read done, result: %v, %v \n", numBytesRead, err)
}

func readfile1b_withOpenReadAndLoop(filename string) {
	fmt.Println("readfile1b_withOpenReadAndLoop")

	file, err := os.Open(filename)
	if err != nil {
		panic(fmt.Errorf("could not open file: %v, error: %v", filename, err))
	}
	// File implements Reader in file.go
	var reader io.Reader = file

	// read using Read with a byte buffer
	bytebuf := make([]byte, 5)
	loopIteration := 1
	for {
		fmt.Printf("loopIteration: %v \n", loopIteration)
		loopIteration++
		numBytesRead, err := reader.Read(bytebuf)
		fmt.Printf("Read done, result: %v, %v \n", numBytesRead, err)
		// This shows us the full buffer, which is actually wrong - Read may leave parts of the buffer untouched if it doesn't need/write those bytes
		fmt.Printf("  bytes as string: %v \n", string(bytebuf))
		// We should really only look at the first 'numBytesRead' bytes of the buffer
		fmt.Printf("  bytes as string (sub slice): %v \n", string(bytebuf[0:numBytesRead]))

		if err != nil {
			if errors.Is(err, io.EOF) {
				fmt.Printf("EOF found, ending loop\n")
				break
			}
			panic(fmt.Errorf("error reading file: %v, error: %v", filename, err))
		}

		if loopIteration > 10 {
			fmt.Printf("-> Safety net, loop has iterated too many times so stopping it \n")
			break
		}

	}
}

func readfile2_os_ReadFile(filename string) {
	fmt.Println("readfile2_os_ReadFile")

	b, err := os.ReadFile(filename)
	if err != nil {
		panic(fmt.Errorf("file not found: %v", filename))
	}
	resultString := string(b)
	fmt.Printf("resultString: %v\n", resultString)
}

func readfile3_withScanner(filename string) {
	fmt.Println("readfile3_withScanner")

	// https://stackoverflow.com/questions/8757389/reading-a-file-line-by-line-in-go

	file, err := os.Open(filename)
	if err != nil {
		panic(fmt.Errorf("could not open file: %v, error: %v", filename, err))
	}
	// File implements Reader in file.go
	var reader io.Reader = file

	scanner := bufio.NewScanner(reader)
	// optionally, resize scanner's capacity for lines over 64K, see next example

	fmt.Printf("Start Scanning\n")
	for scanner.Scan() {
		line := scanner.Text()
		fmt.Printf("line: %v \n", line)
	}
	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}
	fmt.Printf("Ended Scanning\n")
}
