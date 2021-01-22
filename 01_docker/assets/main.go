package main

import (
	"io/ioutil"
	"log"
	"net/http"
	"os"
)

type Handler struct{}

func (h Handler) ServeHTTP(writer http.ResponseWriter, request *http.Request) {
	data, err := ioutil.ReadFile("/proc/1/status")
	if err != nil {
		log.Print(err)
		writer.Header().Set("Content-Type", "text/plain")
		writer.Write([]byte("error occurred"))
		return
	}

	writer.Header().Set("Content-Type", "text/plain")
	writer.Write(data)
}

func main() {
	h := Handler{}
	s := http.Server{
		Addr:    "0.0.0.0:9999",
		Handler: h,
	}
	err := s.ListenAndServe()
	if err != nil {
		log.Print(err)
		os.Exit(1)
	}
}
