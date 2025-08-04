
# Food Ordering Simulation
A multi-user food ordering application built with TCP sockets, JSON messaging (via Gson) and JavaFX.

---

## Overview

This project implements a real-time, multi-user food ordering system consisting of a central `Server` and three distinct `Client` types: `User`, `Restaurant`, and `Courier`. Communication is handled over TCP sockets, where each new connection spawns a dedicated thread on the server. Messages are exchanged as JSON objects, powered by the Gson library.

Users can place orders, track their status in real time, and cancel or repeat orders. Restaurants process incoming orders using a thread pool to simulate multiple workers and notify the server when an order is ready. Couriers accept ready orders, simulate the delivery process, and report back upon completion.

---

## Features
- `User`:
  - Browse restaurants and menus
  - Place, track, cancel, or repeat orders
  - Real-time status updates
- `Restaurant`:
  - Receive and view incoming orders
  - Simulate food preparation via a configurable thread pool
  - Notify the server when orders are ready
- `Courier`:
  - Accept available orders
  - Simulate the delivery process
  - Report delivery completion to the server
- `Server`:
  - Listens on a configurable port (default: `12345`)
  - Each incoming client connection is handled on a separate thread
  - Order messages flow through a central dispatcher, which coordinates between users, restaurants and couriers
    
---

## Technologies

* **JavaFX**: 23.0.1
* **Gson**: 2.10.1

---

## Client Interfaces

### Below are GUI screenshots for each client type.

<img width="670" height="566" alt="Screenshot_1" src="https://github.com/user-attachments/assets/2b453ac0-37d2-44bf-909b-bff1ff28445a" />

### User
<img width="670" height="563" alt="Screenshot_5" src="https://github.com/user-attachments/assets/9be19871-9e32-488e-959e-1cf7516f0a64" />
<img width="670" height="565" alt="Screenshot_2" src="https://github.com/user-attachments/assets/8afd84c8-c9a9-487b-a121-e9279d681e8e" />
<img width="670" height="561" alt="Screenshot_6" src="https://github.com/user-attachments/assets/d277d5d1-a59f-4e30-9097-5c5028be7448" />

### Restaurant
<img width="670" height="557" alt="Screenshot_4" src="https://github.com/user-attachments/assets/890276a1-8b01-4406-9bda-3f45dcb5edde" />

### Courier
<img width="670" height="556" alt="Screenshot_3" src="https://github.com/user-attachments/assets/ae93eb65-8204-472b-a522-a9fbc851fbe4" />

---
