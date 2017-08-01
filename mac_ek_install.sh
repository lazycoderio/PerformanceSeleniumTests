#!/usr/bin/env bash

brew install elasticsearch
elasticsearch-plugin install x-pack
brew install kibana
kibana-plugin install x-pack