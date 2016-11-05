# Work Day Length
Calculates office hours based on RescueTime statistics

# Run

    sbt "runMain WorkDayLength.Cli.CliApp --start-date 2016-11-01 --end-date 2016-11-02"

## Roadmap

### MVP 
1. [X] Tests
2. [X] CLI app returns human-readable data
3. [X] Fix `--help` command
4. [X] Actually exit when finish (no need to `Ctrl+C`)
5. [X] Organize `Settings` package
6. [ ] Sanity checks on real data and fix if needed

### Nice-to-have
1. [ ] CI & Code analytics tools (`scalactic` for example)
2. [ ] CLI running script and `dist` build task
