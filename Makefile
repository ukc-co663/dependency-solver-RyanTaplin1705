all: compile

compile: mvn install
    ./scripts/compile.sh

test: compile
	./scripts/run_tests.sh

clean:
	rm -rf classes