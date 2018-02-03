all: compile

compile: ./scripts/compile.sh

test: compile
	./scripts/run_tests.sh

clean:
	rm -rf classes