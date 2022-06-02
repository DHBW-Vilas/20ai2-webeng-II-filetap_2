<h1 align="center">
    <img src="src/commonMain/resources/F.svg" alt="Logo" width="100" height="100">
</h1>
<div align="center">
<br />

[![Project license](https://img.shields.io/github/license/Sett17/filetap.svg?style=flat-square)](LICENSE)

[![Pull Requests welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg?style=flat-square)](https://github.com/Sett17/filetap/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
[![code with love by Sett17](https://img.shields.io/badge/%3C%2F%3E%20with%20%E2%99%A5%20by-Sett17-ff1414.svg?style=flat-square)](https://github.com/Sett17)

</div>

---

## About

> **[?]**
> Provide general information about your project here.
> What problem does it (intend to) solve?
> What is the purpose of your project?
> Why did you undertake it?
> You don't have to answer all the questions -- just the ones relevant to your project.

<details>
<summary>Screenshots</summary>
<br>

|                                  Desktop                                |                                 Mobile PWA                                  |
| :-------------------------------------------------------------------------: | :-------------------------------------------------------------------------: |
| <img src="https://i.imgur.com/QKwk4lp.png)" title="Home Page" width="100%"> | <img src="https://i.imgur.com/u2KQZHi.png" title="Login Page" width="100%"> |

</details>

### Built With

 - [Kotlin MP](https://kotlinlang.org/docs/multiplatform.html)
 - [KTor](https://github.com/ktorio/ktor)
 - [Faker](https://github.com/serpro69/kotlin-faker)

 - [WebSockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API)

## Getting Started

### Prerequisites

 - Java 8+

## Usage

### Server

Execute `build/install/filetap/bin/filetap` or `build/install/filetap/bin/filetap.bat`, depending on your system, to start the server.

### User

When you access the site, `http://localhost:8000` by default, you will get a random name after connecting to the server.
This name consists of a random color and animal. This your temporary name.

To send a package, the sender needs to input the temporary name of the recipient in the `Recipient` field.
After selecting a file, by clicking the box, the sender can click `Send` to send the package and information to the server.

The Server will then hold the package and asked the recipient to accept the package. Once accepted, the recipient will be downloading the delivered package.

## Diagram

This is the *approximate* Sequence Diagram, to model the information flow.

![mermaid](mermaid-diagram-20220427140832.png)

## License

This project is licensed under the **GNU General Public License v3**.

See [LICENSE](LICENSE) for more information.
