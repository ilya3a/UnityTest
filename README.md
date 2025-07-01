# ConcurrentEventTracker

A lightweight event tracking library built with Kotlin, Room, Retrofit, and Hilt for analytics batching and uploading. Designed for concurrent environments with buffer and policy-based flushing, file compression, and network delivery.

---

## 🧠 Architecture & Concurrency

### Core Components

- **AnalyticsEvent**: The data model representing a single event.
- **AnalyticsEventDao**: Room DAO interface for storing events in SQLite.
- **AnalyticsRepository**: Abstracts access to the database.
- **AnalyticsTracker**: Public interface to track events. Buffers events in memory and flushes based on time or count.
- **AnalyticsFlusher**: Handles flushing events to disk and uploading.
- **FlushPolicy**: Configurable flushing rules (e.g., max buffer size, flush interval).
- **AnalyticsUploadImpl**: Uses Retrofit to upload the gzipped event file to a remote server.
- **MockAnalyticsApi / AnalyticsUploadApi**: Retrofit interfaces for network operations.

### Dependency Injection

- **Hilt** is used throughout the project to provide singletons for DB, Repositories, Retrofit API clients, and the `AnalyticsTracker`.

### Concurrency

- Coroutine-based concurrency using `CoroutineScope` and `Mutex`.
- Events are tracked on background threads with thread-safe buffers.
- Debounced flushing to prevent frequent writes.
- File writing and uploading is done off the main thread.

---

### TODO:
- Allow configuration of `FlushPolicy` values from the application layer.
- build test units


### if i had more time:
- ossification of all unnecessary code
- AnalyticsEvent i will hide from the user and just collect values from him and construct the class in SDK

## 🚀 How to Run

### 1. Clone the Project

```bash
git clone https://github.com/yourname/ConcurrentEventTracker.git
