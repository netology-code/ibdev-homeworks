FROM golang:1.15-alpine AS build
COPY main.go /app/
ENV CGO_ENABLED=0
WORKDIR /app
RUN go build -o server.bin main.go
RUN chmod +x server.bin

FROM alpine
COPY --from=build /app/server.bin /app/
CMD ["/app/server.bin"]