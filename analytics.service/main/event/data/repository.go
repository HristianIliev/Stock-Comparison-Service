package data

import (
	"database/sql"
	"log"
	"strconv"
	"sync"
)

type autoInc struct {
	sync.Mutex // ensures autoInc is goroutine-safe
	id         int
}

func (a *autoInc) ID() (id int) {
	a.Lock()
	defer a.Unlock()

	id = a.id
	a.id++
	return
}

type EventRepository struct {
	db *sql.DB
}

var instanceLock = &sync.Mutex{}

var repositoryInstance *EventRepository

func GetInstance(db *sql.DB) *EventRepository {
	if repositoryInstance == nil {
		instanceLock.Lock()
		defer instanceLock.Unlock()
		if repositoryInstance == nil {
			log.Println("Creating singleton instance of event repository.")

			repositoryInstance = &EventRepository{db: db}
		}
	}

	return repositoryInstance
}

func ClearInstance() {
	repositoryInstance = nil
}

func (repo EventRepository) Load(identifier string) []Event {
	rows, err := repo.db.Query("select username, action, message, entityClass from events where username = ?", identifier)
	if err != nil {
		log.Fatal(err)
	}

	defer rows.Close()

	var (
		username    string
		action      string
		message     string
		entityClass string
	)

	var result []Event

	for rows.Next() {
		err := rows.Scan(&username, &action, &message, &entityClass)
		if err != nil {
			log.Fatal(err)
		}

		result = append(result, Event{Username: username, Action: action, Message: message, EntityClass: entityClass})
	}

	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}

	return result
}

func (repo EventRepository) Store(value Event) bool {
	rows, err := repo.db.Query("SELECT MAX(ID) FROM events")
	if err != nil {
		log.Fatal(err)
	}

	var (
		id int
	)

	for rows.Next() {
		err := rows.Scan(&id)
		if err != nil {
			log.Fatal(err)
		}
	}

	query := "INSERT INTO events VALUES(" + strconv.Itoa(id+1) + ", \"" + value.Username + "\",\"" + value.Action + "\",\"" + value.Message + "\",\"" + value.EntityClass + "\")"

	log.Println(query)

	insert, err := repo.db.Query(query)
	if err != nil {
		panic(err.Error())
	}

	defer insert.Close()

	return true
}
