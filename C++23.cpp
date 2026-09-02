// 编译: g++ -std=c++23 1.cpp -o 1   (需要 GCC 13+ / Clang 16+)
#include <print>       // C++23: std::print / std::println
#include <expected>    // C++23: std::expected
#include <string>
#include <string_view>
#include <vector>
#include <ranges>
#include <algorithm>
// C++23: std::expected 用于返回值或错误，无需异常
std::expected<int, std::string> parse_positive(std::string_view s) {
    int value = 0;
    for (char c : s) {
        if (c < '0' || c > '9')
            return std::unexpected("包含非数字字符");
        value = value * 10 + (c - '0');
    }
    if (value <= 0)
        return std::unexpected("必须为正数");
    return value;
}
int main() {
    // C++23: std::println 直接换行输出，类似 Python 的 print
    std::println("Hello, C++23!");
    // ranges + 管道操作
    std::vector<int> nums{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    auto even_squares = nums
                      | std::views::filter([](int n) { return n % 2 == 0; })
                      | std::views::transform([](int n) { return n * n; });
    std::print("偶数的平方: ");
    for (int v : even_squares)
        std::print("{} ", v);
    std::println("");
    // std::expected 的使用
    for (std::string_view input : {"42", "abc", "-5", "100"}) {
        auto result = parse_positive(input);
        if (result)
            std::println("\"{}\" -> {}", input, *result);
        else
            std::println("\"{}\" -> 错误: {}", input, result.error());
    }
    return 0;
}