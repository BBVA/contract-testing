#!/usr/bin/env bash

: ${PACT_BROKER:?"Need to pass PACT_BROKER variable"}

mvn pact:publish -pl cdc-contracts -DPACT_BROKER=${PACT_BROKER}
