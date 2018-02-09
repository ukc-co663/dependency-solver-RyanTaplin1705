all: compile

compile: deps
	./scripts/compile.sh

deps:
	./scripts/install_deps.sh
	touch deps

test: compile
	./scripts/run_tests.sh

clean:
	rm -rf classes

reallyclean: clean
	rm -rf lib deps

.PHONY: all compile test clean reallyclean