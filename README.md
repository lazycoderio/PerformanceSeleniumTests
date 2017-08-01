[![Join the chat at https://gitter.im/lazycoderio/PerformanceSeleniumTests](https://badges.gitter.im/lazycoderio/PerformanceSeleniumTests.svg)](https://gitter.im/lazycoderio/sample-java-jenkins?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![codecov](https://codecov.io/gh/lazycoderio/Basic-Selenium-Java/branch/master/graph/badge.svg)](https://codecov.io/gh/lazycoderio/Basic-Selenium-Java) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/afdb9236cb2442faaa513c2db4114396)](https://www.codacy.com/app/andrew-m-krug/Basic-Selenium-Java?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lazycoderio/Basic-Selenium-Java&amp;utm_campaign=Badge_Grade) [![Dependency Status](https://www.versioneye.com/user/projects/583b58fd4ef164003ff45522/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/583b58fd4ef164003ff45522)  [![Stories in Ready](https://badge.waffle.io/lazycoderio/PerformanceSeleniumTests.svg?label=ready&title=Ready)](http://waffle.io/lazycoderio/PerformanceSeleniumTests)

# Using this Project

This project is using the latest Selenium Bindings Jest for Connecting to Elasticsearch and the InfluxDB client.

Bowsermob Proxy is used to capture the network requests.

More features and samples coming soon. Check out the [Waffle Board](http://waffle.io/lazycoderio/PerformanceSeleniumTests)

## Setup on Mac OS

Copy this bootstrap script and run it in a Terminal shell window:

   ```
   sh -c "$(curl -fsSL https://raw.githubusercontent.com/lazycoderio/Basic-Selenium-Java/master/mac-bootstrap.sh)"
   ```

It installs the following if it is not already installed:

1. Install Homebrew `/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`
2. Install Caskroom `brew tap caskroom/cask`
3. If you dont have the Java Development Kit (JDK) run this command `brew cask install java`
4. Install Maven `brew install maven`
5. Run `./mac_install_browsers.sh`
6. You will need to install  either the TICK or ELK stack

The [mac_ek_install.sh](mac_ek_install.sh) installs elasticsearch and kibana

The [mac_tick_install.sh](mac_tick_install.sh) installs the tick stack
