# Mini Factory Automation Simulator

**A Java-based educational simulation tool for understanding industrial automation and object-oriented programming**

---

## 📌 Overview

The **Mini Factory Automation Simulator** is a software project developed to simulate the operation of a small automated factory. Built using Java and Object-Oriented Programming (OOP) principles, this simulator allows users to model and interact with key components of a production line — such as machines, conveyor belts, sensors, and workers — in a safe, hardware-free environment.

This project was designed by 3rd-year Software Engineering students at Addis Ababa Science and Technology University as part of their academic coursework. It serves both as a learning tool for understanding real-world automation systems and as a practical application of core software engineering concepts.

> ✅ Perfect for students exploring OOP, system design, and industrial automation!

---

## 🎯 Purpose & Goals

Modern factories use complex automated systems that are expensive and difficult to access for learning purposes. This simulator bridges that gap by offering:

- A **low-cost**, **interactive way** to explore how automated factories work.
- Hands-on experience with **object-oriented design** (encapsulation, inheritance, polymorphism).
- A clear example of how software can model real-world processes like manufacturing workflows.

### Key Objectives:
- Model factory components (machines, belts, sensors) as reusable Java classes.
- Simulate item flow through a production line.
- Allow user control via a simple console interface.
- Demonstrate OOP principles in a realistic context.

---

## 🔧 Features

The simulator supports the following functionalities:

| Feature | Description |
|--------|-------------|
| **Component Simulation** | Machines, moving belts, sensors, and items behave like real factory elements. |
| **User Control** | Start, pause, resume, or stop the simulation anytime. |
| **Dynamic Interaction** | Inject new products into the line during runtime. |
| **Machine Management** | Manually start, stop, or trigger errors on machines. |
| **Status Monitoring** | View current states of all components (e.g., idle, running, blocked). |
| **Event Logging** | Real-time logs show important events like jams, completions, and errors. |
| **Scenario Loading** | Load predefined factory setups for different simulations. |

---

## 🛠️ System Architecture

The system follows a clean **layered architecture** to separate concerns and improve maintainability:

1. **Presentation Layer** – Console-based menu for user interaction.
2. **Application (Logic) Layer** – Core simulation engine managing machines, items, and events.
3. **Data Layer** – Stores logs, machine states, and session history in a database.

Components communicate through a central **Factory Controller**, which coordinates actions based on sensor inputs and user commands.

> 💡 Designed with scalability in mind — easy to extend with GUI (e.g., JavaFX) later!

---

## 🧱 Key Concepts Demonstrated

This project emphasizes foundational software engineering practices suitable for intermediate learners:

| Concept | How It's Used |
|-------|---------------|
| **Encapsulation** | Each component hides its internal state (e.g., machine status). |
| **Inheritance** | Base `Machine` class extended into specific types (e.g., Cutter, Assembler). |
| **Polymorphism** | Different machines process items in unique ways but share common methods. |
| **Modularity** | Every component (belt, sensor, worker) is implemented as an independent class. |
| **Event Handling** | Sensors detect changes and notify the controller asynchronously. |

These make it easier to understand how large systems are built from small, interacting parts.

---

## 🖥️ How to Use

### Running the Simulator
1. Compile all Java files:
   ```bash
   javac *.java
   ```
2. Run the main class:
   ```bash
   java Main
   ```
3. Follow the on-screen menu to:
   - Load a factory scenario
   - Start/pause the simulation
   - Add items manually
   - Monitor machine statuses
   - View event logs

> 📝 All interactions happen through a text-based console — no advanced setup needed!

---

## 📁 Project Structure

```
src/
├── Machine.java        // Base machine class and subclasses
├── ConveyorBelt.java   // Handles item movement between stations
├── Sensor.java         // Detects item arrival, jams, etc.
├── Worker.java         // Optional human element for manual tasks
├── Item.java           // Represents products being processed
├── FactoryController.java // Central coordinator
├── DatabaseManager.java  // Logs and stores simulation data
├── UserInterface.java    // Console menu handler
└── Main.java           // Entry point
```

---

## 📚 Learning Value

This project is ideal for 3rd-year computer science or software engineering students because it:

- Applies classroom theory (OOP, SDLC, UML) to a tangible problem.
- Uses **Agile development** methodology for iterative improvement.
- Includes UML diagrams (Use Case, Class, Architecture) for better visualization.
- Encourages structured thinking about system behavior and component interaction.

It’s not just code — it’s a complete mini-system you can study, modify, and expand.

---

## ⚠️ Limitations

Please note:
- This is a **software-only simulation** — no physical hardware involved.
- Workflow is simplified compared to real industrial systems.
- Current UI is console-based; GUI support can be added in future versions.

---

## 📂 Future Improvements

Potential enhancements include:
- Adding a **JavaFX graphical interface**.
- Supporting **real-time data visualization**.
- Integrating **cloud logging or remote monitoring**.
- Expanding to support **Industry 4.0** concepts like IoT-style communication.

---

## 🙌 Acknowledgments

Developed by:  
Abel Mekonnen, Amira Abdurahman, Barok Yeshiber, Bekam Yoseph, Bemigbar Yehuwalawork, Betelhem Kassaye  
Department of Software Engineering, Section A  
Addis Ababa Science and Technology University

Supervised by: **Abdi Muleta**

Submission Date: November 15, 2025

---

## 📚 References

- [Autodesk – The Future of Factory Automation](https://www.autodesk.com)
- [OMG Unified Modeling Language Specification](https://www.omg.org/spec/UML/)
- Pressman, R. & Maxim, B. *Software Engineering: A Practitioner’s Approach*
- Fowler, M. *UML Distilled*

---

> 🔗 For more details, check out the full project documentation and diagrams:
> - [System Architecture Diagram](https://lucid.app/lucidchart/435d13da-8f79-40e3-9cd0-9ab55912ac50/edit)
> - [User Interface Flow](https://www.mermaidchart.com/app/projects/10d48619-8f9f-4f56-a2ab-719183eb9723/diagrams/0462b740-1932-4de7-bb28-8525b2ab1e5e/version/v0.1/edit)

---

🎓 **Learn. Simulate. Build.**  
This project proves that powerful learning tools don’t need expensive equipment — just good design and clear thinking.
