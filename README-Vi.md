# Minh họa release flow
Repository này sẽ minh hoạ release flow thông qua một project Task Manager đơn giản sử dụng Java.

## Về project
Project là chương trình quản lý công việc đơn giản cho phép người dùng tạo, đọc, cập nhật và xoá tasks từ commandline.

## Các bước thực hiện

### 0. Project khởi đầu
Đầu tiên, fork repository này và clone về máy local.
```bash
git clone https://github.com/VoidKeishi/ReleaseFlowDemonstration.git
cd ReleaseFlowDemonstration
git checkout starter
```

### 1. Thêm tính năng xem danh sách công việc

Checkout một branch mới cho việc phát triển tính năng này.
```bash
git checkout -b feature/view-tasks
```

Thêm tính năng này vào class `TaskManager`:
```java
private void viewTasks() {
    if (tasks.isEmpty()) {
        System.out.println("No tasks available.");
    } else {
        System.out.println("\nTasks:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + ". " + task.getDescription());
        }
    }
}
```

Test tính năng bằng cách chạy project:
```bash
javac TaskManager.java
java TaskManager
```

Lưu ý rằng khi chạy sẽ tạo ra các file class trong thư mục project. Thêm các file class này vào file `.gitignore`:
```
*.class
```

Commit các thay đổi và push branch lên repository trên GitHub:
```bash
git add .
git commit -m "Add view tasks feature"
git push origin feature/view-tasks
```

Tạo pull request trên GitHub và merge branch feature vào branch `main`. Sau khi pull request được merge, checkout branch `main` và pull các thay đổi:
```bash
git checkout main
git pull
```

### 2. Thêm mức độ ưu tiên cho các công việc

Checkout một branch mới cho việc sửa đổi này. Cần pull các thay đổi mới nhất từ branch `main`:
```bash
git pull origin main
git checkout -b topic/task-priority
```

Thêm trường priority vào class `Task`:
```java
private int priority; // 1: High, 2: Medium, 3: Low
```

Chỉnh sửa constructor để bao gồm trường priority:
```java
public Task(String description, int priority) {
    this.description = description;
    this.priority = priority;
}
```

Thêm getter method cho trường priority:
```java
public int getPriority() {
    return priority;
}
```

Cập nhật method `addTask` để bao gồm priority:
```java
private void addTask() {
    System.out.print("Enter task description: ");
    String description = scanner.nextLine();
    System.out.print("Enter priority (1: High, 2: Medium, 3: Low): ");
    int priority = scanner.nextInt();
    scanner.nextLine(); // Consume newline
    tasks.add(new Task(description, priority));
    System.out.println("Task added.");
}
```

Cập nhật method `viewTasks` để hiển thị priority của mỗi task:
```java
private void viewTasks() {
    if (tasks.isEmpty()) {
        System.out.println("No tasks available.");
    } else {
        System.out.println("\nTasks:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + ". " + task.getDescription() + " (Priority: " + task.getPriority() + ")");
        }
    }
}
```

Test tính năng bằng cách chạy project, commit các thay đổi, push branch, tạo pull request, merge pull request, và pull các thay đổi về branch `main`. (Tương tự như bước 1)

### 3. Sửa lỗi khi mô tả công việc để trống

Checkout một branch mới để sửa lỗi. Cần pull các thay đổi mới nhất từ branch `main`:
```bash
git checkout -b bugfix/empty-task-description
```

Cập nhật method `addTask` để kiểm tra trường hợp description bị để trống:
```java
private void addTask() {
    String description;
    do {
        System.out.print("Enter task description: ");
        description = scanner.nextLine();
        if (description.isEmpty()) {
            System.out.println("Description cannot be empty. Please try again.");
        }
    } while (description.isEmpty());

    System.out.print("Enter priority (1: High, 2: Medium, 3: Low): ");
    int priority = scanner.nextInt();
    scanner.nextLine(); // Consume newline
    tasks.add(new Task(description, priority));
    System.out.println("Task added.");
}
```

Test tính năng bằng cách chạy project, commit các thay đổi, push branch, tạo pull request, merge pull request, và pull các thay đổi về branch `main`. (Tương tự như bước 1)

### 4. Phát hành phiên bản 1.0

Sau khi hoàn thành một số tính năng cơ bản, đã đến lúc release phiên bản đầu tiên của project. Bắt đầu bằng cách tạo một release branch từ branch `main`:
```bash
git checkout -b release/1.0
git push origin release/1.0
```

Bây giờ người dùng có thể tải mã nguồn từ branch release và chạy project.

## Ghi chú thêm
Nếu quên pull các thay đổi mới nhất từ remote branch trước khi commit các thay đổi, có thể pull nếu không có xung đột bằng cách rebase:
```bash
git pull --rebase
```
Nếu có xung đột, cần phải giải quyết thủ công.

## Lợi ích của release flow

1. **Phát triển có tổ chức**: Việc sử dụng branch giúp các developer có thể làm việc trên nhiều tính năng và sửa lỗi song song mà không ảnh hưởng đến branch `main` ổn định.
2. **Hỗ trợ làm việc nhóm**: Pull request hỗ trợ việc review code, đảm bảo chất lượng code tốt hơn và giảm nguy cơ phát sinh lỗi.
3. **Quản lý phiên bản rõ ràng**: Release branch cung cấp một cấu trúc rõ ràng cho việc quản lý phiên bản, giúp dễ dàng theo dõi và quản lý các thay đổi cho từng phiên bản.
4. **Giảm thiểu rủi ro**: Việc cô lập các thay đổi vào các branch feature hoặc bugfix giúp giảm nguy cơ làm mất ổn định project.
5. **Quay lại dễ dàng**: Nếu một commit gây ra các vấn đề nghiêm trọng, việc xác định và đảo ngược các thay đổi trở nên dễ dàng hơn.

## Kết luận
Bài thực hành này minh họa một luồng release flow đơn giản sử dụng branch, pull request và versioning. Release flow giúp việc phát triển các tính năng mới, sửa lỗi và phát hành diễn ra theo cách có tổ chức và hiệu quả.

