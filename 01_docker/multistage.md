# MultiStage Building

MultiStage Build подразумевает, что разработчики (или вы) можете использовать результаты работы, полученные при работе в контейнере (например, файлы), построенном из одного образа, для построения другого образа.

Яркий пример: когда мы с вами программировали на Go, нам нужно было установить Go, скомпилировать приложение, а потом запустить.

Почему бы не попробовать сделать это с помощью Docker?

Мы создадим образ на базе образа, в котором установлен Go, соберём наше приложение и используем результаты сборки для построения финального образа.

В нашем случае это будет выглядеть вот так:
1. Берём образ, в котором установлена нужная нам версия Go
1. Создаём на базе него свой образ, в который копируем все наши файлы (а именно - сервер)
1. Запускаем из образа контейнер (на самом деле, это сделает сам Docker), в котором и производим сборку
1. Результаты сборки копируем в новый образ

```dockerfile
FROM golang:1.15-alpine AS build
COPY main.go /app/
ENV CGO_ENABLED=0
WORKDIR /app
RUN go build -o server.bin main.go
RUN chmod +x server.bin

FROM alpine
COPY --from=build /app/server.bin /app/
CMD ["/app/server.bin"]
```

<details>
<summary>Содержимое файла main.go</summary>

Вам достаточно скопировать и сохранить под именем `main.go`:
```go
package main

import (
	"io/ioutil"
	"log"
	"net/http"
	"os"
)

type Handler struct { }

func (h Handler) ServeHTTP(writer http.ResponseWriter, request *http.Request)  {
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
```
</details>

Давайте рассмотрим те моменты, которые мы ещё не рассматривали:
* `AS build` - даём имя образу, собираемому на данном этапе (чтобы использовать его на следующих этапах сборки)
* `ADD . /app` - добавляем всё содержимое текущего каталога `.` (а поскольку сам файл будет в корне репо, то все файлы) в каталог `/app` в образе
* `ENV` - устанавливаем значение переменной окружения
* `WORKDIR` - устанавливаем рабочий каталог для следующих команд
* `RUN` - запускаем сборку
* `COPY --from=build` - копируем файл из образа, созданного на одной из предыдущих стадий

Образы:
* [golang:1-15-alpine](https://hub.docker.com/_/golang) - образ с дистрибутивом Alpine Linux и установленным Go версии 1.15.
* [alpine:3.7](https://hub.docker.com/_/golang) - образ с дистрибутивом Alpine Linux последней версии.

[Alpine Linux](https://alpinelinux.org) - это минималистичный (весом всего около 5 Мб) образ, который очень любят использовать разработчики в качестве базового (для сравнения: образ Ubuntu весит 72 Мб и это ещё очень мало, некоторые образы достигают нескольких сотен мегабайт вплоть до гигабайта).

Вот, в принципе, и всё.

Теперь попробуем это собрать:
```shell script
docker build -t ibdevserver .
docker container run -p 9999:9999 ibdevserver
```

После этого можете в браузере открыть `localhost` на порту 9999 (или `docker-machine ip default`, если у вас Docker Toolbox).

Для тех, кто использует Play With Docker, необходимо открыть порт с помощью кнопки `OPEN PORT` (браузер сам откроет новую вкладку):

![](pic/open-port.png)

![](pic/server.png)

На лекции мы с вами говорили, что руками каждый раз набирать команды не особо удобно, поэтому мы можем "упаковать" все эти инструкции в `docker-compose.yml`:

```yml
version: '3.7'
services:
  server:
    build: .
    image: ibdevserver
    ports:
      - 9999:9999
```

И запускать всё одной командой: `docker-compose up --build` (убедитесь, что до этого вы остановили запущенный контейнер).

Детальную информацию о файле Docker Compose вы можете найти в [официальной документации](https://github.com/compose-spec/compose-spec/blob/master/spec.md).

Все файлы, описанные в этом документе расположены в каталоге [assets](assets).
