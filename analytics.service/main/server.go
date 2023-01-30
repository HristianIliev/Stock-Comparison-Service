package main

import (
	"analytics.service/main/event/data"
	"analytics.service/main/request"
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
	"log"
	"net/http"
)

func main() {
	db, err := sql.Open("mysql", "root:Abcd1234@tcp(127.0.0.1:3306)/analytics")
	if err != nil {
		panic(err.Error())
	}

	defer db.Close()

	eventRepository := data.GetInstance(db)

	mux := http.NewServeMux()
	mux.Handle("/", request.New(eventRepository))

	s := http.Server{
		Addr:    ":8085",
		Handler: mux,
	}

	log.Println("Server is started and is listening on port localhost:8085")

	log.Fatal(s.ListenAndServe())
}
