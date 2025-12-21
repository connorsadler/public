package cfsutils

import (
	"fmt"
	"log"
	"strings"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestFileExtension1(t *testing.T) {
	filename := "TestFileExtension.json"
	result := FileExtension(filename)
	assert.Equal(t, ".json", result)
}

func TestFileExtension2(t *testing.T) {
	filename := "TestFileExtension.something.json"
	result := FileExtension(filename)
	assert.Equal(t, ".json", result)
}

func TestFileExtension3_fileHasNoExtension(t *testing.T) {
	filename := "TestFileExtensionNoExtension"
	result := FileExtension(filename)
	assert.Equal(t, "", result)
}

func TestFFileNameWithoutExt1(t *testing.T) {
	filename := "TestFileExtension.json"
	result := FileNameWithoutExt(filename)
	assert.Equal(t, "TestFileExtension", result)
}

func TestFFileNameWithoutExt2(t *testing.T) {
	filename := "TestFileExtension.something.json"
	result := FileNameWithoutExt(filename)
	assert.Equal(t, "TestFileExtension.something", result)
}

func TestRenameFileToBackupFile(t *testing.T) {

	// Setup a file in a temp dir
	dir := fmt.Sprintf("./testdata/temp/helpers/%v", time.Now().UnixMilli())
	ensureDirExists(dir)
	filename1_startOfName := "TestRenameFileToBackupFile_1001"
	filename1 := dir + "/" + filename1_startOfName + ".json"
	WriteToFile(filename1, "hello world 1001")
	filename2_startOfName := "TestRenameFileToBackupFile_1002"
	filename2 := dir + "/" + filename2_startOfName
	WriteToFile(filename2, "hello world 1002")

	filesInDir, err := ListFilesInDir(dir)
	if err != nil {
		log.Fatalf("error listing files in dir, err: %v", err)
	}
	assert.Equal(t, 2, len(filesInDir))

	// Run test
	RenameFileToBackupFile(filename1)
	RenameFileToBackupFile(filename2)

	// Checks
	filesInDir, err = ListFilesInDir(dir)
	if err != nil {
		log.Fatalf("error listing files in dir, err: %v", err)
	}
	log.Printf("filesInDir: %v\n", filesInDir)
	assert.Equal(t, 4, len(filesInDir))
	for _, fileInDir := range filesInDir {
		fileExt := FileExtension(fileInDir)

		if strings.HasPrefix(fileInDir, filename1_startOfName) {
			// filename1 and it's backup file both have '.json' extension
			assert.Equal(t, ".json", fileExt)
		} else if strings.HasPrefix(fileInDir, filename2_startOfName) {
			// filename2 has no extension, but it's backup file does e.g. ".20241013_125930"
			assert.Regexp(t, "^$|^\\.[0-9]{8}_[0-9]{6}$", fileExt)
		} else {
			assert.Fail(t, "Unrecognised filename: "+fileInDir)
		}

	}
}

// Input file has a blank line at the end
func TestReadFileToLines0_1(t *testing.T) {

	lines, err := ReadFileToLines0("testdata/TestReadFileToLines1.txt")
	if err != nil {
		log.Fatalf("error in TestReadFileToLines, err: %v", err)
	}

	expected := []string{"Hello", "from", "Connor", ""}
	assert.Equal(t, expected, lines)
}

// Input file does NOT have a blank line at the end
func TestReadFileToLines0_2(t *testing.T) {

	lines, err := ReadFileToLines0("testdata/TestReadFileToLines2.txt")
	if err != nil {
		log.Fatalf("error in TestReadFileToLines, err: %v", err)
	}

	expected := []string{"Hello", "from", "Connor"}
	assert.Equal(t, expected, lines)
}

// - Blank line in middle of file
// - Some blank lines at the end of the file (all 4 of which are included in the result)
func TestReadFileToLines0_3(t *testing.T) {

	lines, err := ReadFileToLines0("testdata/TestReadFileToLines3.txt")
	if err != nil {
		log.Fatalf("error in TestReadFileToLines, err: %v", err)
	}

	expected := []string{"Hello", "from", "Connor", "", "Blank line in middle of file", "", "", "", ""}
	assert.Equal(t, expected, lines)
}

// Input file has a blank line at the end
// Note: Blank line at end is NOT included in result
func TestReadFileToLines1(t *testing.T) {

	lines, err := ReadFileToLines("testdata/TestReadFileToLines1.txt")
	if err != nil {
		log.Fatalf("error in TestReadFileToLines, err: %v", err)
	}

	// Note: Blank line at end is NOT included in result
	expected := []string{"Hello", "from", "Connor"}
	assert.Equal(t, expected, lines)
}

// Input file does NOT have a blank line at the end
func TestReadFileToLines2(t *testing.T) {

	lines, err := ReadFileToLines("testdata/TestReadFileToLines2.txt")
	if err != nil {
		log.Fatalf("error in TestReadFileToLines, err: %v", err)
	}

	expected := []string{"Hello", "from", "Connor"}
	assert.Equal(t, expected, lines)
}

// - Blank line in middle of file
// - Some blank lines at the end of the file (only 3 of which are included in the result)
func TestReadFileToLines3(t *testing.T) {

	lines, err := ReadFileToLines("testdata/TestReadFileToLines3.txt")
	if err != nil {
		log.Fatalf("error in TestReadFileToLines, err: %v", err)
	}

	expected := []string{"Hello", "from", "Connor", "", "Blank line in middle of file", "", "", ""}
	assert.Equal(t, expected, lines)
}
