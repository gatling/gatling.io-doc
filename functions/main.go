package main

import (
	"encoding/csv"
	"encoding/json"
	"fmt"
	"log"
	"os"
)

type KeyValuePair struct {
	Key string `json:"key"`
	Value string `json:"value"`
}

type KeyValuePairs struct {
	Pairs []KeyValuePair `json:"data"`
}

func csvToJson(data [][]string) KeyValuePairs {
	var pairs []KeyValuePair
	for _, line := range data {
		var pair KeyValuePair
		for j, field := range line {
			if j == 0 {
				pair.Key = field
			} else if j == 1 {
				pair.Value = field
			}
		}
		pairs = append(pairs, pair)
	}
	kvpairs := KeyValuePairs{pairs}
	return kvpairs
}

func main() {
	f, err := os.Open("redirects.csv")
	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()

	reader := csv.NewReader(f)
	csvData, err := reader.ReadAll()
	if err != nil {
		log.Fatal(err)
	}

	jsonData := csvToJson(csvData)
	jsonFormatted, err := json.MarshalIndent(jsonData, "", "  ")
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(string(jsonFormatted))
}
