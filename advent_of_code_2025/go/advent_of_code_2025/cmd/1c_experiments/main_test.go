package main

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestStringArrayInit(t *testing.T) {

	stringsReadingDownColumn := make([]string, 3)
	assert.Equal(t, "", stringsReadingDownColumn[0])
	assert.Equal(t, "", stringsReadingDownColumn[1])
	assert.Equal(t, "", stringsReadingDownColumn[2])
	// TODOx: Check panic on this line - is that even possible?
	//	assert.Equal(t, "", stringsReadingDownColumn[3])

}
