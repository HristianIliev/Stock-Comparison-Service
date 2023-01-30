package request

import (
	"analytics.service/main/event/data"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

type RequestRouter struct {
	eventRepository *data.EventRepository
}

func New(eventRepository *data.EventRepository) RequestRouter {
	return RequestRouter{eventRepository: eventRepository}
}

func (r RequestRouter) ServeHTTP(response http.ResponseWriter, request *http.Request) {
	log.Println("Request method is [" + request.Method + "]")

	if request.Method != "GET" {
		log.Printf("Server can handle only GET Requests\n")

		response.WriteHeader(http.StatusNotFound)

		return
	}

	username := request.URL.Query().Get("username")
	if username == "" {
		log.Printf("Server can handle only GET Requests\n")

		response.WriteHeader(http.StatusBadRequest)

		return
	}

	action := request.URL.Query().Get("action")

	var result []data.Event

	events := r.eventRepository.Load(username)
	for _, event := range events {
		if event.Username == username {
			if action != "" && event.Action == action {
				result = append(result, event)
			} else if action == "" {
				result = append(result, event)
			}
		}
	}

	j, err := json.MarshalIndent(result, "", "    ")
	if err != nil {
		fmt.Printf("Error: %s", err.Error())
	} else {
		fmt.Fprintf(response, string(j))
		fmt.Println(string(j))
	}
}
