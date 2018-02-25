all: compile

compile: dependants
	./scripts/compile.sh

dependants:
	./scripts/install_deps.sh
	touch dependants

test: compile
	./scripts/run_tests.sh

clean:
	rm -rf classes

reallyclean: clean
	rm -rf lib dependants

.PHONY: all compile test clean reallyclean