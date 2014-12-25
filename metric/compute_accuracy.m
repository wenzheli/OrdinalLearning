function accu = compute_accuracy(intervals, L, data, y, va_data, va_y)
% compute the accuracy
total = size(va_data);
correct = 0;