package cfsutils

import (
	"bufio"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"io/fs"
	"log"
	"os"
	"path/filepath"
	"strings"
	"time"
)

func FileExists(name string) bool {
	fileExists, err := FileExistsErr(name)
	if err != nil {
		log.Fatalf("error checking file exists, name: %v, err: %v", name, err)
	}
	return fileExists
}

func ListFilesInDir(dir string) ([]string, error) {
	f, err := os.Open(dir)
	if err != nil {
		return nil, err
	}
	defer f.Close()

	fns, err := f.Readdirnames(0)
	if err != nil {
		return nil, err
	}
	//log.Printf("[dbg] Readdirnames => %v\n", fns)

	return fns, nil
}

func FileExistsErr(name string) (bool, error) {
	_, err := os.Stat(name)
	if err == nil {
		return true, nil
	}
	if errors.Is(err, os.ErrNotExist) {
		return false, nil
	}
	return false, err
}

// return value includes the dot e.g. ".json"
func FileExtension(filename string) string {
	return filepath.Ext(filename)
}

func FileNameWithoutExt(filename string) string {
	return filename[:len(filename)-len(FileExtension(filename))]
}

// The backup filename will have the same extension as the original, if any
func RenameFileToBackupFile(filename string) {
	contents, err := os.ReadFile(filename)
	if err != nil {
		log.Printf("Error reading file, could not back it up, filename: %v, err: %v", filename, err)
		return
	}

	// Create backup filename - Note: We maintain the original file extension, if any
	fileNameNoExtension := FileNameWithoutExt(filename)
	fileExtension := FileExtension(filename) // e.g. json
	backupFilename := fmt.Sprintf("%v.%v%v", fileNameNoExtension, GetDateTimeStamp(), fileExtension)

	err = WriteToFile(backupFilename, string(contents))
	if err != nil {
		log.Printf("Error writing to backup file, could not back it up, filename: %v, backupFilename: %v, err: %v", filename, backupFilename, err)
	}
}

func ReadFile(filename string) (string, error) {
	b, err := os.ReadFile(filename)
	if err != nil {
		return "", err
	}
	return string(b), nil
}

func removeTrailing(str string, delim rune) string {
	if strings.HasSuffix(str, string(delim)) {
		return strings.TrimSuffix(str, string(delim))
	} else {
		return str
	}
}

// ReadFileToLines0
// v0.1 - read line by line using bufio.Reader
func ReadFileToLines0(filename string) ([]string, error) {

	// open file
	reader, err := os.Open(filename)
	if err != nil {
		return nil, fmt.Errorf("ReadFileToLines - error during open for read: %v", err)
	}

	result := make([]string, 0)

	bufReader := bufio.NewReader(reader)
	for {
		strFromFile, err := bufReader.ReadString('\n')
		//fmt.Printf("strFromFile: %v\n", strFromFile)
		//fmt.Printf("err: %v\n", err)
		if err != nil {
			if errors.Is(err, io.EOF) { // prefered way by GoLang doc
				result = append(result, removeTrailing(strFromFile, '\n'))
				break
			}
			return nil, fmt.Errorf("ReadFileToLines0 - Error during read: %v", err)
		}
		result = append(result, removeTrailing(strFromFile, '\n'))
	}

	return result, nil
}

// ReadFileToLines
// Uses Scanner so does NOT include the final blank line at the end, if it is present
// v0.2 - use Scanner ... see: https://stackoverflow.com/questions/8757389/reading-a-file-line-by-line-in-go
func ReadFileToLines(filename string) ([]string, error) {

	// open file
	reader, err := os.Open(filename)
	if err != nil {
		return nil, fmt.Errorf("ReadFileToLines - error during open for read: %v", err)
	}

	result := make([]string, 0)

	scanner := bufio.NewScanner(reader)
	// optionally, resize scanner's capacity for lines over 64K, see next example
	for scanner.Scan() {
		result = append(result, scanner.Text())
	}
	if err := scanner.Err(); err != nil {
		return nil, fmt.Errorf("ReadFileToLines - Error during read using scanner: %v", err)
	}

	return result, nil
}

func GetDateTimeStamp() string {
	t := time.Now().UTC()
	formatted := fmt.Sprintf("%d%02d%02d_%02d%02d%02d",
		t.Year(), t.Month(), t.Day(),
		t.Hour(), t.Minute(), t.Second())

	return formatted
}

func WriteToFile(filename string, contents string) error {
	file, err := os.Create(filename)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()
	// log.Println("File created")

	// Create buffered writer and write to the file
	bufferedWriter := bufio.NewWriter(file)
	_, err = bufferedWriter.WriteString(contents)
	if err != nil {
		return err
	}
	// log.Printf("Bytes written: %d\n", bytesWritten)

	// Write memory buffer to disk
	bufferedWriter.Flush()

	return nil
}

func ensureDirExists(dir string) error {
	return os.MkdirAll(dir, fs.ModePerm)
}

func ConvToJsonString(obj interface{}) string {
	builder := &strings.Builder{}
	err := json.NewEncoder(builder).Encode(obj)
	if err != nil {
		log.Fatal(err)
	}
	return builder.String()
}

// pretty print
func ConvToJsonStringIndent(obj interface{}) string {
	res, err := json.MarshalIndent(obj, "", "  ")
	if err != nil {
		log.Fatal(err)
	}
	return string(res)
}

func ConvFromJsonString(jsonString string, target any) {
	// message := new(Message)
	// messageJson := `{ "historyId" : "12345", "id" : "MESSAGE_ID_12345" }`
	// fmt.Printf("start with messageJson: %v\n", messageJson)
	err := json.NewDecoder(strings.NewReader(jsonString)).Decode(target)
	if err != nil {
		log.Fatal(err)
	}
}

type SleepExContext struct {
	sleepSecs     int
	currentSecond int
	continueSleep bool // can be set to false in the hook function to abort the sleep
}

func SleepEx(sleepSecs int, msgFormatEverySecond string) {
	SleepExWithFunc(sleepSecs, func(context *SleepExContext) {
		fmt.Printf(msgFormatEverySecond, context.currentSecond, context.sleepSecs)
		context.continueSleep = true
	})
}

func SleepEx2(sleepSecs int, msgFormatEverySecond string, continueSleepFunc func() bool) {
	SleepExWithFunc(sleepSecs, func(context *SleepExContext) {
		fmt.Printf(msgFormatEverySecond, context.currentSecond, context.sleepSecs)
		context.continueSleep = continueSleepFunc()
	})
}

// return: true if we did the full sleep, false if it was aborted
func SleepExWithFunc(sleepSecs int, msgEverySecond func(*SleepExContext)) bool {
	context := &SleepExContext{}
	context.sleepSecs = sleepSecs
	for i := 1; i <= sleepSecs; i++ {
		context.currentSecond = i
		msgEverySecond(context)
		if !context.continueSleep {
			return false
		}
		time.Sleep(1 * time.Second)
	}
	return true
}
